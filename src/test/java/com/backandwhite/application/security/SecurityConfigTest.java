package com.backandwhite.application.security;

import com.backandwhite.domain.repository.OauthClientRepository;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class SecurityConfigTest {

        private final SecurityConfig config = new SecurityConfig(new BCryptPasswordEncoder());

        @Test
        void corsConfigurationSource_includesExpectedOriginsAndMethods() {
                CorsConfigurationSource source = config.corsConfigurationSource();
                CorsConfiguration cors = source.getCorsConfiguration(new MockHttpServletRequest());

                assertThat(cors).isNotNull();
                assertThat(cors.getAllowedOrigins()).isNotEmpty();
                assertThat(cors.getAllowedOrigins()).allSatisfy(origin -> assertThat(origin).matches("https?://.*"));
                assertThat(cors.getAllowedMethods()).contains("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH");
                assertThat(cors.getAllowedHeaders()).isNotEmpty();
                assertThat(cors.getExposedHeaders()).contains("Set-Cookie", "x-auth-token");
                assertThat(cors.getAllowCredentials()).isTrue();
                assertThat(cors.getMaxAge()).isEqualTo(3600L);
        }

        @Test
        void registeredClientRepository_returnsInMemoryWhenNoOauthClientRepository() {
                ObjectProvider<OauthClientRepository> provider = mock(ObjectProvider.class);
                when(provider.getIfAvailable()).thenReturn(null);

                RegisteredClientRepository repository = config.registeredClientRepository(provider);

                assertThat(repository).isNotNull();
                assertThat(repository).isInstanceOf(
                                org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository.class);
        }

        @Test
        void registeredClientRepository_returnsCustomWhenOauthClientRepositoryAvailable() {
                OauthClientRepository mockRepository = mock(OauthClientRepository.class);
                ObjectProvider<OauthClientRepository> provider = mock(ObjectProvider.class);
                when(provider.getIfAvailable()).thenReturn(mockRepository);

                RegisteredClientRepository repository = config.registeredClientRepository(provider);

                assertThat(repository).isNotNull();
                assertThat(repository).isInstanceOf(CustomRegisteredClientRepository.class);
        }

        @Test
        void jwkSource_providesRsaKeyWithPrivateKey() throws Exception {
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
