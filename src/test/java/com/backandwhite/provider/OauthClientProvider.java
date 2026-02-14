package com.backandwhite.provider;

import com.backandwhite.domain.model.OauthClient;
import com.backandwhite.infrastructure.db.postgres.entity.OauthClientEntity;

import java.util.List;

public final class OauthClientProvider {

    public static final Long CLIENT_ID = 1L;
    public static final String CLIENT_CLIENT_ID = "client-app";
    public static final String CLIENT_SECRET = "client-secret";

    public static final Long OTHER_CLIENT_ID = 2L;
    public static final String OTHER_CLIENT_CLIENT_ID = "client-admin";
    public static final String OTHER_CLIENT_SECRET = "client-secret-2";

    private OauthClientProvider() {
        // Utility class.
    }

    public static OauthClient oauthClient() {
        return OauthClient.builder()
                .id(CLIENT_ID)
                .clientId(CLIENT_CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .scopes(List.of(ScopeProvider.readScope()))
                .redirectUris(List.of(RedirectUriProvider.redirectUri()))
                .grantTypes(List.of(GrantTypeProvider.grantType()))
                .build();
    }

    public static OauthClient otherOauthClient() {
        return OauthClient.builder()
                .id(OTHER_CLIENT_ID)
                .clientId(OTHER_CLIENT_CLIENT_ID)
                .clientSecret(OTHER_CLIENT_SECRET)
                .scopes(List.of(ScopeProvider.writeScope()))
                .redirectUris(List.of(RedirectUriProvider.otherRedirectUri()))
                .grantTypes(List.of(GrantTypeProvider.otherGrantType()))
                .build();
    }

    public static OauthClientEntity oauthClientEntity() {
        return OauthClientEntity.builder()
                .id(CLIENT_ID)
                .clientId(CLIENT_CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .scopes(List.of(ScopeProvider.scopeEntity()))
                .redirectUris(List.of(RedirectUriProvider.redirectUriEntity()))
                .grantTypes(List.of(GrantTypeProvider.grantTypeEntity()))
                .build();
    }
}
