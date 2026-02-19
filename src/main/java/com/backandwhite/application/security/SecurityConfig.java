package com.backandwhite.application.security;

import com.backandwhite.domain.repository.OauthClientRepository;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    private Environment env;

    public SecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${app.security.handler-url:http://localhost:4200}")
    private String handlerUrl;

    private final String[] GET_PUBLIC_URLS = {
            "/login",
            "/login.html",
            "/forgot-password.html",
            "/register.html",
            "/terms.html",
            "/css/**",
            "/js/**",
            "/images/**",
            "/favicon.ico",
            "/error",
            "/.well-known/**",
            "/oauth2/token/**",
            "/logout",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/api/v1/**"};

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();

        http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .with(authorizationServerConfigurer,
                        authorizationServer -> authorizationServer
                                .oidc(Customizer.withDefaults()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(GET_PUBLIC_URLS).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)));
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(GET_PUBLIC_URLS).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/oauth2/**", "/login",
                        "/logout"))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(
                                HttpStatus.NO_CONTENT)))
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(authenticationSuccessHandler())
                        .permitAll());
        return http.build();
    }

    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler() {
        SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
        if (Objects.nonNull(env.getProperty("active.profil")) && Objects.equals(env.getProperty("active.profil"), "pro")) {
            handlerUrl = env.getProperty("app.security.handler-url-1");
        } else {
            handlerUrl = env.getProperty("app.security.handler-url-2");
        }
        handler.setDefaultTargetUrl(handlerUrl);
        handler.setAlwaysUseDefaultTargetUrl(true);
        return handler;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "https://backandwhite.com",
                "http://localhost:4200",
                "https://webapp-production-68d2.up.railway.app",
                "https://mic-authservice-production.up.railway.app"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*")); // Permitir todos los headers
        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "x-auth-token"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // Cache preflight por 1 hora
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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
        return RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("default-client")
                .clientSecret(passwordEncoder.encode("secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("default")
                .build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    public org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService authorizationService() {
        return new org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService();
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
            JwtGrantedAuthoritiesConverter delegate)
            implements Converter<Jwt, Collection<GrantedAuthority>> {

        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Collection<GrantedAuthority> authorities = delegate.convert(jwt);

            List<String> rolesFromClaim = (List<String>) jwt.getClaims().get("roles");
            if (rolesFromClaim != null) {
                rolesFromClaim.stream()
                        .map(SimpleGrantedAuthority::new)
                        .forEach(authorities::add);
            }
            return authorities;
        }
    }
}