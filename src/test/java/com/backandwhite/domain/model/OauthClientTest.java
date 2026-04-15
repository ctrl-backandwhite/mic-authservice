package com.backandwhite.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.provider.GrantTypeProvider;
import com.backandwhite.provider.RedirectUriProvider;
import com.backandwhite.provider.ScopeProvider;
import java.util.List;
import org.junit.jupiter.api.Test;

class OauthClientTest {

    @Test
    void addScope_appendsToExistingScopes() {
        OauthClient client = OauthClient.builder().build();
        Scope scope = ScopeProvider.readScope();

        client.addScope(List.of(scope));

        assertThat(client.getScopes()).containsExactly(scope);
    }

    @Test
    void removeScope_removesFromExistingScopes() {
        Scope scope = ScopeProvider.readScope();
        OauthClient client = OauthClient.builder().build();
        client.addScope(List.of(scope, ScopeProvider.writeScope()));

        client.removeScope(List.of(scope));

        assertThat(client.getScopes()).containsExactly(ScopeProvider.writeScope());
    }

    @Test
    void addRedirectUri_appendsToExistingRedirectUris() {
        OauthClient client = OauthClient.builder().build();
        RedirectUri redirectUri = RedirectUriProvider.redirectUri();

        client.addRedirectUri(List.of(redirectUri));

        assertThat(client.getRedirectUris()).containsExactly(redirectUri);
    }

    @Test
    void removeRedirectUri_removesFromExistingRedirectUris() {
        RedirectUri uri = RedirectUriProvider.redirectUri();
        RedirectUri otherUri = RedirectUriProvider.otherRedirectUri();
        OauthClient client = OauthClient.builder().build();
        client.addRedirectUri(List.of(uri, otherUri));

        client.removeRedirectUri(List.of(uri));

        assertThat(client.getRedirectUris()).containsExactly(otherUri);
    }

    @Test
    void addGrantType_appendsToExistingGrantTypes() {
        OauthClient client = OauthClient.builder().build();
        GrantType grantType = GrantTypeProvider.grantType();

        client.addGrantType(List.of(grantType));

        assertThat(client.getGrantTypes()).containsExactly(grantType);
    }

    @Test
    void removeGrantType_removesFromExistingGrantTypes() {
        GrantType gt = GrantTypeProvider.grantType();
        GrantType otherGt = GrantTypeProvider.otherGrantType();
        OauthClient client = OauthClient.builder().build();
        client.addGrantType(List.of(gt, otherGt));

        client.removeGrantType(List.of(gt));

        assertThat(client.getGrantTypes()).containsExactly(otherGt);
    }
}
