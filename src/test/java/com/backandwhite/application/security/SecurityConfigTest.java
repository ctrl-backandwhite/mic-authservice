package com.backandwhite.application.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.backandwhite.domain.repository.OauthClientRepository;
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
                mock(RateLimitFilter.class));
        ReflectionTestUtils.setField(config, "jwtSecret",
                "local-secret-key-change-me-in-production-must-be-256-bits-long");
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
    void jwtAuthenticationConverter_addsRolesFromClaim() {
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "RS256").claim("roles", List.of("ROLE_ADMIN", "ROLE_USER"))
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(300)).build();

        Collection<GrantedAuthority> authorities = config.jwtAuthenticationConverter().convert(jwt).getAuthorities();

        assertThat(authorities).extracting(GrantedAuthority::getAuthority).contains("ROLE_ADMIN", "ROLE_USER");
    }
}
