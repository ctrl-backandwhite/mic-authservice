package com.backandwhite.application.security;

import com.backandwhite.domain.repository.OauthClientRepository;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SecurityConfigTest {

    @Test
    void corsConfigurationSource_includesExpectedOriginsAndMethods() {
        SecurityConfig config = new SecurityConfig(mock(PasswordEncoder.class));

        CorsConfigurationSource source = config.corsConfigurationSource();
        CorsConfiguration cors = source.getCorsConfiguration(new MockHttpServletRequest());

        assertThat(cors).isNotNull();
        assertThat(cors.getAllowedOrigins()).contains(
                "http://localhost:4200",
                "https://webapp-production-68d2.up.railway.app",
                "https://mic-authservice-production.up.railway.app");
        assertThat(cors.getAllowedMethods()).contains("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH");
        assertThat(cors.getAllowedHeaders()).contains("*");
        assertThat(cors.getExposedHeaders()).contains("Set-Cookie", "x-auth-token");
        assertThat(cors.getAllowCredentials()).isTrue();
        assertThat(cors.getMaxAge()).isEqualTo(3600L);
    }

    @Test
    void registeredClientRepository_returnsInMemoryWhenNoOauthClientRepository() {
        SecurityConfig config = new SecurityConfig(mock(PasswordEncoder.class));

        ObjectProvider<OauthClientRepository> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(null);

        RegisteredClientRepository repository = config.registeredClientRepository(provider);

        assertThat(repository).isNotNull();
        assertThat(repository.getClass().getSimpleName()).isEqualTo("InMemoryRegisteredClientRepository");
    }

    @Test
    void registeredClientRepository_returnsCustomWhenOauthClientRepositoryAvailable() {
        SecurityConfig config = new SecurityConfig(mock(PasswordEncoder.class));

        OauthClientRepository mockRepository = mock(OauthClientRepository.class);
        ObjectProvider<OauthClientRepository> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(mockRepository);

        RegisteredClientRepository repository = config.registeredClientRepository(provider);

        assertThat(repository).isNotNull();
        assertThat(repository).isInstanceOf(CustomRegisteredClientRepository.class);
    }

    @Test
    void jwkSource_providesRsaKeyWithPrivateKey() throws Exception {
        SecurityConfig config = new SecurityConfig(mock(PasswordEncoder.class));

        JWKSource<SecurityContext> source = config.jwkSource();
        RSAKey rsaKey = (RSAKey) source
                .get(new com.nimbusds.jose.jwk.JWKSelector(
                                new com.nimbusds.jose.jwk.JWKMatcher.Builder().build()),
                        null)
                .get(0);

        assertThat(rsaKey).isNotNull();
        assertThat(rsaKey.toRSAPublicKey()).isNotNull();
        assertThat(rsaKey.toRSAPrivateKey()).isNotNull();
    }

    @Test
    void jwtAuthenticationConverter_addsRolesFromClaim() {
        SecurityConfig config = new SecurityConfig(mock(PasswordEncoder.class));

        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("roles", List.of("ROLE_ADMIN", "ROLE_USER"))
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();

        Collection<GrantedAuthority> authorities = config.jwtAuthenticationConverter().convert(jwt)
                .getAuthorities();

        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_ADMIN", "ROLE_USER");
    }
}
