package com.backandwhite.application.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.backandwhite.application.port.out.AuthEventPort;
import com.backandwhite.application.port.out.NotificationEventPort;
import com.backandwhite.domain.repository.OauthClientRepository;
import com.backandwhite.domain.repository.RoleRepository;
import com.backandwhite.domain.repository.UserRepository;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.test.util.ReflectionTestUtils;

class SecurityConfigTest {

    private final SecurityConfig config;

    SecurityConfigTest() {
        config = new SecurityConfig(new BCryptPasswordEncoder(), mock(SessionRevokingLogoutHandler.class),
                mock(RateLimitFilter.class), mock(NotificationEventPort.class), mock(UserRepository.class),
                mock(RoleRepository.class), mock(AuthEventPort.class));
        ReflectionTestUtils.setField(config, "jwtSecret",
                "local-secret-key-change-me-in-production-must-be-256-bits-long");
    }

    @Test
    void registeredClientRepository_returnsInMemoryWhenNoOauthClientRepository() {
        ObjectProvider<OauthClientRepository> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(null);

        RegisteredClientRepository repository = config.registeredClientRepository(provider);

        assertThat(repository).isNotNull().isInstanceOf(
                org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository.class);
    }

    @Test
    void registeredClientRepository_returnsCustomWhenOauthClientRepositoryAvailable() {
        OauthClientRepository mockRepository = mock(OauthClientRepository.class);
        ObjectProvider<OauthClientRepository> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(mockRepository);

        RegisteredClientRepository repository = config.registeredClientRepository(provider);

        assertThat(repository).isNotNull().isInstanceOf(CustomRegisteredClientRepository.class);
    }

    @Test
    void jwtAuthenticationConverter_addsRolesFromClaim() {
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "RS256").claim("roles", List.of("ROLE_ADMIN", "ROLE_USER"))
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(300)).build();

        Collection<GrantedAuthority> authorities = config.jwtAuthenticationConverter().convert(jwt).getAuthorities();

        assertThat(authorities).extracting(GrantedAuthority::getAuthority).contains("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void jwtAuthenticationConverter_noRolesClaim_returnsBaseAuthorities() {
        // roles claim absent → only the JwtGrantedAuthoritiesConverter scopes apply.
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "RS256").claim("scope", "read").issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300)).build();

        Collection<GrantedAuthority> authorities = config.jwtAuthenticationConverter().convert(jwt).getAuthorities();

        assertThat(authorities).extracting(GrantedAuthority::getAuthority).noneMatch(a -> a.startsWith("ROLE_"));
    }

    @Test
    void authorizationServerSettings_blankIssuer_returnsDefaults() {
        ReflectionTestUtils.setField(config, "issuer", "");

        var settings = config.authorizationServerSettings();

        assertThat(settings).isNotNull();
    }

    @Test
    void authorizationServerSettings_nullIssuer_returnsDefaults() {
        ReflectionTestUtils.setField(config, "issuer", null);

        var settings = config.authorizationServerSettings();

        assertThat(settings).isNotNull();
    }

    @Test
    void authorizationServerSettings_explicitIssuer_appliesIt() {
        ReflectionTestUtils.setField(config, "issuer", "https://issuer.test/auth");

        var settings = config.authorizationServerSettings();

        assertThat(settings.getIssuer()).isEqualTo("https://issuer.test/auth");
    }

    @Test
    void logoutSuccessHandler_jsonAcceptHeader_returnsNoContent() throws Exception {
        var handler = config.logoutSuccessHandler();
        org.springframework.mock.web.MockHttpServletRequest request = new org.springframework.mock.web.MockHttpServletRequest();
        org.springframework.mock.web.MockHttpServletResponse response = new org.springframework.mock.web.MockHttpServletResponse();
        request.addHeader("Accept", "application/json");

        handler.onLogoutSuccess(request, response, null);

        assertThat(response.getStatus()).isEqualTo(204);
    }

    @Test
    void logoutSuccessHandler_browserDefault_redirectsToHome() throws Exception {
        var handler = config.logoutSuccessHandler();
        org.springframework.mock.web.MockHttpServletRequest request = new org.springframework.mock.web.MockHttpServletRequest();
        org.springframework.mock.web.MockHttpServletResponse response = new org.springframework.mock.web.MockHttpServletResponse();
        // No Accept header → wantsJson=false → redirect path
        handler.onLogoutSuccess(request, response, null);

        assertThat(response.getStatus()).isIn(302, 200);
        assertThat(response.getRedirectedUrl()).isEqualTo("/");
    }

    @Test
    void logoutSuccessHandler_jsonAcceptWithRedirectUri_redirects() throws Exception {
        var handler = config.logoutSuccessHandler();
        org.springframework.mock.web.MockHttpServletRequest request = new org.springframework.mock.web.MockHttpServletRequest();
        org.springframework.mock.web.MockHttpServletResponse response = new org.springframework.mock.web.MockHttpServletResponse();
        request.addHeader("Accept", "application/json");
        request.setParameter("redirect_uri", "/store");

        handler.onLogoutSuccess(request, response, null);

        // hasRedirect=true → uses redirect handler even with json accept
        assertThat(response.getRedirectedUrl()).isEqualTo("/store");
    }

    @Test
    void logoutSuccessHandler_protocolRelativeRedirect_blocked() throws Exception {
        var handler = config.logoutSuccessHandler();
        org.springframework.mock.web.MockHttpServletRequest request = new org.springframework.mock.web.MockHttpServletRequest();
        org.springframework.mock.web.MockHttpServletResponse response = new org.springframework.mock.web.MockHttpServletResponse();
        request.addHeader("Accept", "application/json");
        // // is open-redirect — must be rejected → falls back to JSON 204
        request.setParameter("redirect_uri", "//evil.com");

        handler.onLogoutSuccess(request, response, null);

        assertThat(response.getStatus()).isEqualTo(204);
    }

    @Test
    void authenticationSuccessHandler_returnsConfiguredHandler() {
        ReflectionTestUtils.setField(config, "handlerUrl", "/welcome");

        var handler = config.authenticationSuccessHandler();

        assertThat(handler).isNotNull();
    }

    @Test
    void authenticationFailureHandler_returnsHandler() {
        var handler = config.authenticationFailureHandler();

        assertThat(handler).isNotNull();
    }

    @Test
    void googleOAuth2SuccessHandler_returnsConfiguredHandler() {
        ReflectionTestUtils.setField(config, "handlerUrl", "/welcome");

        var handler = config.googleOAuth2SuccessHandler();

        assertThat(handler).isNotNull();
    }

    @Test
    void jwtEncoder_returnsNimbusEncoder() {
        var encoder = config.jwtEncoder();

        assertThat(encoder).isNotNull();
    }

    @Test
    void jwtDecoder_returnsNimbusDecoder() {
        var decoder = config.jwtDecoder();

        assertThat(decoder).isNotNull();
    }
}
