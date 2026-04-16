package com.backandwhite.application.security;

import com.backandwhite.application.port.out.NotificationEventPort;
import com.backandwhite.domain.repository.OauthClientRepository;
import com.backandwhite.domain.repository.UserRepository;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jackson.SecurityJacksonModules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String LOGIN_PATH = "/login";

    private final PasswordEncoder passwordEncoder;
    private final SessionRevokingLogoutHandler sessionRevokingLogoutHandler;
    private final RateLimitFilter rateLimitFilter;
    private final NotificationEventPort notificationEventPort;
    private final UserRepository userRepository;

    public SecurityConfig(PasswordEncoder passwordEncoder, SessionRevokingLogoutHandler sessionRevokingLogoutHandler,
            RateLimitFilter rateLimitFilter, NotificationEventPort notificationEventPort,
            UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.sessionRevokingLogoutHandler = sessionRevokingLogoutHandler;
        this.rateLimitFilter = rateLimitFilter;
        this.notificationEventPort = notificationEventPort;
        this.userRepository = userRepository;
    }

    @Value("${app.security.handler-url:http://localhost:4200}")
    private String handlerUrl;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    private final String[] publicUrls = {LOGIN_PATH, "/login.html", "/forgot-password.html", "/register.html",
            "/terms.html", "/activation-success.html", "/activation-error.html", "/reset-password.html",
            "/reset-success.html", "/reset-error.html", "/css/**", "/js/**", "/font/**", "/images/**", "/favicon.ico",
            "/error", "/.well-known/**", "/oauth2/token/**", "/logout", "/swagger-ui/**", "/v3/api-docs/**",
            "/swagger-ui.html"};

    private final String[] publicApiUrls = {"/api/v1/users/register", "/api/v1/users/forgot-password",
            "/api/v1/users/reset-password"};

    private final String[] publicApiGetUrls = {"/api/v1/users/activate"};

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http, JwtEncoder jwtEncoder,
            @Autowired(required = false) OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer) {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();

        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
        if (jwtCustomizer != null) {
            jwtGenerator.setJwtCustomizer(jwtCustomizer);
        }
        DelegatingOAuth2TokenGenerator tokenGenerator = new DelegatingOAuth2TokenGenerator(jwtGenerator,
                new OAuth2AccessTokenGenerator(), new OAuth2RefreshTokenGenerator());

        http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher()).cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .with(authorizationServerConfigurer,
                        authorizationServer -> authorizationServer.oidc(Customizer.withDefaults())
                                .tokenGenerator(tokenGenerator))
                .authorizeHttpRequests(authorize -> authorize.requestMatchers(publicUrls).permitAll()
                        .requestMatchers(HttpMethod.POST, publicApiUrls).permitAll()
                        .requestMatchers(HttpMethod.GET, publicApiGetUrls).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll().requestMatchers("/api/v1/**")
                        .authenticated().anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
                        new LoginUrlAuthenticationEntryPoint(LOGIN_PATH),
                        new MediaTypeRequestMatcher(MediaType.TEXT_HTML)));
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.requestMatchers(publicUrls).permitAll()
                        .requestMatchers(HttpMethod.POST, publicApiUrls).permitAll()
                        .requestMatchers(HttpMethod.GET, publicApiGetUrls).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll().requestMatchers("/api/v1/**")
                        .authenticated().anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .logout(logout -> logout.logoutUrl("/logout").addLogoutHandler(sessionRevokingLogoutHandler)
                        .invalidateHttpSession(true).clearAuthentication(true).deleteCookies("JSESSIONID")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)))
                .formLogin(form -> form.loginPage(LOGIN_PATH).successHandler(authenticationSuccessHandler())
                        .failureHandler(authenticationFailureHandler()).permitAll())
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CustomAuthenticationSuccessHandler authenticationSuccessHandler() {
        CustomAuthenticationSuccessHandler handler = new CustomAuthenticationSuccessHandler(notificationEventPort,
                userRepository);
        handler.setDefaultTargetUrl(handlerUrl);
        handler.setAlwaysUseDefaultTargetUrl(false);
        return handler;
    }

    @Bean
    public CustomAuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler(notificationEventPort, userRepository);
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(
            ObjectProvider<OauthClientRepository> oauthClientRepositoryProvider) {
        OauthClientRepository oauthClientRepository = oauthClientRepositoryProvider.getIfAvailable();
        if (oauthClientRepository == null) {
            return new InMemoryRegisteredClientRepository(defaultRegisteredClient());
        }
        return new CustomRegisteredClientRepository(oauthClientRepository);
    }

    private RegisteredClient defaultRegisteredClient() {
        return RegisteredClient.withId(UUID.randomUUID().toString()).clientId("default-client")
                .clientSecret(passwordEncoder.encode("secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS).scope("default").build();
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        SecretKey key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return new NimbusJwtEncoder(new ImmutableSecret<>(key));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKey key = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    public org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService authorizationService(
            org.springframework.jdbc.core.JdbcOperations jdbcOperations,
            RegisteredClientRepository registeredClientRepository) {

        ClassLoader classLoader = JdbcOAuth2AuthorizationService.class.getClassLoader();

        BasicPolymorphicTypeValidator.Builder typeValidatorBuilder = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("com.backandwhite.");

        List<JacksonModule> modules = SecurityJacksonModules.getModules(classLoader, typeValidatorBuilder);
        JsonMapper jsonMapper = JsonMapper.builder().addModules(modules).build();

        var rowMapper = new JdbcOAuth2AuthorizationService.JsonMapperOAuth2AuthorizationRowMapper(
                registeredClientRepository, jsonMapper);

        var service = new JdbcOAuth2AuthorizationService(jdbcOperations, registeredClientRepository);
        service.setAuthorizationRowMapper(rowMapper);
        return service;
    }

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                new CustomJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter));
        return jwtAuthenticationConverter;
    }

    private record CustomJwtGrantedAuthoritiesConverter(
            JwtGrantedAuthoritiesConverter delegate) implements Converter<Jwt, Collection<GrantedAuthority>> {

        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Collection<GrantedAuthority> authorities = delegate.convert(jwt);

            List<String> rolesFromClaim = (List<String>) jwt.getClaims().get("roles");
            if (rolesFromClaim != null) {
                rolesFromClaim.stream().map(SimpleGrantedAuthority::new).forEach(authorities::add);
            }
            return authorities;
        }
    }
}
