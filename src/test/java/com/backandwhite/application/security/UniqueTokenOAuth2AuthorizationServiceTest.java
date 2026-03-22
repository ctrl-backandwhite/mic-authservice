package com.backandwhite.application.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.security.Principal;
import java.util.List;

import static com.backandwhite.application.security.UniqueTokenOAuth2AuthorizationService.DELETE_PREVIOUS_SQL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UniqueTokenOAuth2AuthorizationServiceTest {

    @Mock
    private JdbcOperations jdbcOperations;

    @Mock
    private OAuth2AuthorizationService delegate;

    private UniqueTokenOAuth2AuthorizationService service;

    @BeforeEach
    void setUp() {
        service = new UniqueTokenOAuth2AuthorizationService(jdbcOperations, delegate);
    }

    @Test
    @SuppressWarnings("unchecked")
    void save_withAccessToken_deletesExistingAndDelegates() {
        OAuth2Authorization authorization = mock(OAuth2Authorization.class);
        OAuth2Authorization.Token<OAuth2AccessToken> tokenWrapper = mock(OAuth2Authorization.Token.class);

        when(authorization.getAccessToken()).thenReturn(tokenWrapper);
        when(authorization.getRegisteredClientId()).thenReturn("client-1");
        when(authorization.getPrincipalName()).thenReturn("user@example.com");
        when(authorization.getId()).thenReturn("auth-id-123");

        service.save(authorization);

        verify(jdbcOperations).update(
                eq(DELETE_PREVIOUS_SQL),
                eq("client-1"),
                eq("user@example.com"),
                eq("auth-id-123"));
        verify(delegate).save(authorization);
    }

    @Test
    void save_withoutAccessToken_skipsDeleteAndDelegates() {
        OAuth2Authorization authorization = mock(OAuth2Authorization.class);
        when(authorization.getAccessToken()).thenReturn(null);

        service.save(authorization);

        verify(jdbcOperations, never()).update(anyString(), any(), any(), any());
        verify(delegate).save(authorization);
    }

    @Test
    void remove_delegatesToService() {
        OAuth2Authorization authorization = mock(OAuth2Authorization.class);

        service.remove(authorization);

        verify(delegate).remove(authorization);
        verifyNoInteractions(jdbcOperations);
    }

    @Test
    void findById_delegatesToService() {
        OAuth2Authorization expected = mock(OAuth2Authorization.class);
        when(delegate.findById("some-id")).thenReturn(expected);

        OAuth2Authorization result = service.findById("some-id");

        assertThat(result).isSameAs(expected);
        verify(delegate).findById("some-id");
    }

    @Test
    void findByToken_withAccessTokenType_delegatesToService() {
        OAuth2Authorization expected = mock(OAuth2Authorization.class);
        when(delegate.findByToken("token-value", OAuth2TokenType.ACCESS_TOKEN)).thenReturn(expected);

        OAuth2Authorization result = service.findByToken("token-value", OAuth2TokenType.ACCESS_TOKEN);

        assertThat(result).isSameAs(expected);
        verify(delegate).findByToken("token-value", OAuth2TokenType.ACCESS_TOKEN);
    }

    @Test
    void findByToken_withRefreshTokenType_delegatesToService() {
        OAuth2Authorization expected = mock(OAuth2Authorization.class);
        when(delegate.findByToken("rt-value", OAuth2TokenType.REFRESH_TOKEN)).thenReturn(expected);

        OAuth2Authorization result = service.findByToken("rt-value", OAuth2TokenType.REFRESH_TOKEN);

        assertThat(result).isSameAs(expected);
    }

    @Test
    @SuppressWarnings("unchecked")
    void save_withAccessToken_sqlContainsAccessTokenNullCheck() {
        OAuth2Authorization authorization = mock(OAuth2Authorization.class);
        OAuth2Authorization.Token<OAuth2AccessToken> tokenWrapper = mock(OAuth2Authorization.Token.class);

        when(authorization.getAccessToken()).thenReturn(tokenWrapper);
        when(authorization.getRegisteredClientId()).thenReturn("my-client");
        when(authorization.getPrincipalName()).thenReturn("john@example.com");
        when(authorization.getId()).thenReturn("new-id");

        service.save(authorization);

        verify(jdbcOperations).update(
                contains("access_token_value IS NOT NULL"),
                anyString(), anyString(), anyString());
    }

    // ------------------------------------------------------------------
    // simplifyPrincipal tests
    // ------------------------------------------------------------------

    @Test
    void simplifyPrincipal_returnsAsIs_whenNoPrincipalAttribute() {
        OAuth2Authorization authorization = mock(OAuth2Authorization.class);
        when(authorization.getAttribute(Principal.class.getName())).thenReturn(null);

        OAuth2Authorization result = service.simplifyPrincipal(authorization);

        assertThat(result).isSameAs(authorization);
    }

    @Test
    void simplifyPrincipal_returnsAsIs_whenPrincipalAlreadyString() {
        OAuth2Authorization authorization = mock(OAuth2Authorization.class);
        Authentication auth = UsernamePasswordAuthenticationToken.authenticated(
                "admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(authorization.getAttribute(Principal.class.getName())).thenReturn(auth);

        OAuth2Authorization result = service.simplifyPrincipal(authorization);

        // Principal is already a String → returns same instance
        assertThat(result).isSameAs(authorization);
    }

    @Test
    void simplifyPrincipal_replacesComplexPrincipal() {
        // Simulate a domain User object as the principal
        Object complexPrincipal = new Object() {
            @Override
            public String toString() {
                return "admin@test.com";
            }
        };

        // Create an auth token with the complex principal
        Authentication complexAuth = new UsernamePasswordAuthenticationToken(
                complexPrincipal, null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_USER")));

        // Build a real OAuth2Authorization with the complex auth in attributes
        RegisteredClient registeredClient = RegisteredClient.withId("rc-1")
                .clientId("test-client")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("http://localhost/callback")
                .build();

        OAuth2Authorization original = OAuth2Authorization.withRegisteredClient(registeredClient)
                .principalName("admin@test.com")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .attribute(Principal.class.getName(), complexAuth)
                .build();

        OAuth2Authorization result = service.simplifyPrincipal(original);

        // Must be a different instance
        assertThat(result).isNotSameAs(original);

        // Principal stored in attributes must now be a String
        Authentication simplified = result.getAttribute(Principal.class.getName());
        assertThat(simplified).isNotNull();
        assertThat(simplified.getPrincipal()).isInstanceOf(String.class);
        assertThat(simplified.getName()).isEqualTo("admin@test.com");

        // Authorities must be SimpleGrantedAuthority
        assertThat(simplified.getAuthorities())
                .hasSize(2)
                .allSatisfy(a -> assertThat(a).isInstanceOf(SimpleGrantedAuthority.class));
        assertThat(simplified.getAuthorities())
                .extracting("authority")
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }
}
