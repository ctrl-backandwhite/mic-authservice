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
                .grantTypes(List.of(GrantType.builder().value("client_credentials").build()))
                .build();

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
}
