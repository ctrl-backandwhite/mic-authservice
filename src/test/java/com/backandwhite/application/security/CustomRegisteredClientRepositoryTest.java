package com.backandwhite.application.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.backandwhite.domain.model.GrantType;
import com.backandwhite.domain.model.OauthClient;
import com.backandwhite.domain.model.RedirectUri;
import com.backandwhite.domain.model.Scope;
import com.backandwhite.domain.repository.OauthClientRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

@ExtendWith(MockitoExtension.class)
class CustomRegisteredClientRepositoryTest {

    @Mock
    private OauthClientRepository oauthClientRepository;

    @InjectMocks
    private CustomRegisteredClientRepository repository;

    @Test
    void findByClientId_existingClient_returnsRegisteredClient() {
        OauthClient client = OauthClient.builder().id(1L).clientId("app-client").clientSecret("secret")
                .scopes(List.of(Scope.builder().uniqueName("openid").build()))
                .redirectUris(List.of(RedirectUri.builder().value("https://example.com/callback").build()))
                .grantTypes(List.of(GrantType.builder().value("authorization_code").build(),
                        GrantType.builder().value("refresh_token").build()))
                .build();

        when(oauthClientRepository.findByClientId("app-client")).thenReturn(client);

        RegisteredClient result = repository.findByClientId("app-client");

        assertThat(result).isNotNull();
        assertThat(result.getClientId()).isEqualTo("app-client");
        assertThat(result.getScopes()).contains("openid");
        assertThat(result.getRedirectUris()).contains("https://example.com/callback");
    }

    @Test
    void findByClientId_missingClient_returnsNull() {
        when(oauthClientRepository.findByClientId("missing")).thenReturn(null);

        RegisteredClient result = repository.findByClientId("missing");

        assertThat(result).isNull();
    }

    @Test
    void findById_existingClient_returnsRegisteredClient() {
        OauthClient client = OauthClient.builder().id(1L).clientId("app-client").clientSecret("secret")
                .scopes(List.of(Scope.builder().uniqueName("openid").build()))
                .redirectUris(List.of(RedirectUri.builder().value("https://example.com/callback").build()))
                .grantTypes(List.of(GrantType.builder().value("client_credentials").build())).build();

        when(oauthClientRepository.getById(1L)).thenReturn(client);

        RegisteredClient result = repository.findById("1");

        assertThat(result).isNotNull();
        assertThat(result.getClientId()).isEqualTo("app-client");
    }

    @Test
    void findById_invalidFormat_returnsNull() {
        RegisteredClient result = repository.findById("not-a-number");

        assertThat(result).isNull();
    }

    @Test
    void findById_missingClient_returnsNull() {
        when(oauthClientRepository.getById(999L)).thenReturn(null);

        RegisteredClient result = repository.findById("999");

        assertThat(result).isNull();
    }

    @Test
    void save_throwsUnsupportedOperation() {
        assertThrows(UnsupportedOperationException.class, () -> repository.save(null));
    }

    @Test
    void findByClientId_clientWithNullRedirectsAndScopes_mapsSafely() {
        // Spring's RegisteredClient.build() requires at least one grant type, so we
        // can't null *all* collections, but we can verify the null-redirectUris and
        // null-scopes branches.
        OauthClient client = OauthClient.builder().id(2L).clientId("minimal-client").clientSecret("secret")
                .grantTypes(List.of(GrantType.builder().value("client_credentials").build())).redirectUris(null)
                .scopes(null).build();

        when(oauthClientRepository.findByClientId("minimal-client")).thenReturn(client);

        RegisteredClient result = repository.findByClientId("minimal-client");

        assertThat(result).isNotNull();
        assertThat(result.getClientId()).isEqualTo("minimal-client");
        assertThat(result.getRedirectUris()).isEmpty();
        assertThat(result.getScopes()).isEmpty();
    }

    @Test
    void findByClientId_unknownGrantType_isIgnored() {
        OauthClient client = OauthClient.builder().id(3L).clientId("weird-grant").clientSecret("s")
                .grantTypes(List.of(GrantType.builder().value("password").build(),
                        GrantType.builder().value("REFRESH_TOKEN").build()))
                .redirectUris(List.of(RedirectUri.builder().value("https://example.com/cb").build()))
                .scopes(List.of(Scope.builder().uniqueName("read").build())).build();

        when(oauthClientRepository.findByClientId("weird-grant")).thenReturn(client);

        RegisteredClient result = repository.findByClientId("weird-grant");

        assertThat(result).isNotNull();
        assertThat(result.getAuthorizationGrantTypes()).extracting(g -> g.getValue()).contains("refresh_token");
    }
}
