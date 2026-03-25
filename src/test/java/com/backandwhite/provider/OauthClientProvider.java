package com.backandwhite.provider;

import com.backandwhite.api.dto.in.OauthClientDtoIn;
import com.backandwhite.api.dto.out.OauthClientDtoOut;
import com.backandwhite.domain.model.OauthClient;
import com.backandwhite.infrastructure.db.postgres.entity.OauthClientEntity;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public final class OauthClientProvider {

    public static final Long CLIENT_ID = 1L;
    public static final String CLIENT_CLIENT_ID = "client-app";
    public static final String CLIENT_SECRET = "client-secret";

    public static final Long OTHER_CLIENT_ID = 2L;
    public static final String OTHER_CLIENT_CLIENT_ID = "client-admin";
    public static final String OTHER_CLIENT_SECRET = "client-secret-2";


    public static OauthClient oauthClient() {
        return OauthClient.builder()
                .id(CLIENT_ID)
                .clientId(CLIENT_CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .scopes(List.of(ScopeProvider.openidScope()))
                .redirectUris(List.of(RedirectUriProvider.redirectUri()))
                .grantTypes(List.of(GrantTypeProvider.grantType()))
                .createdAt(AuditProvider.CREATED_AT)
                .updatedAt(AuditProvider.UPDATED_AT)
                .createdBy(AuditProvider.CREATED_BY)
                .updatedBy(AuditProvider.UPDATED_BY)
                .build();
    }

    public static OauthClient otherOauthClient() {
        return OauthClient.builder()
                .id(OTHER_CLIENT_ID)
                .clientId(OTHER_CLIENT_CLIENT_ID)
                .clientSecret(OTHER_CLIENT_SECRET)
                .scopes(List.of(ScopeProvider.profileScope()))
                .redirectUris(List.of(RedirectUriProvider.otherRedirectUri()))
                .grantTypes(List.of(GrantTypeProvider.otherGrantType()))
                .createdAt(AuditProvider.CREATED_AT)
                .updatedAt(AuditProvider.UPDATED_AT)
                .createdBy(AuditProvider.CREATED_BY)
                .updatedBy(AuditProvider.UPDATED_BY)
                .build();
    }

    public static OauthClientEntity oauthClientEntity() {
        return OauthClientEntity.builder()
                .id(CLIENT_ID)
                .clientId(CLIENT_CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .scopes(List.of(ScopeProvider.openidScopeEntity()))
                .redirectUris(List.of(RedirectUriProvider.redirectUriEntity()))
                .grantTypes(List.of(GrantTypeProvider.grantTypeEntity()))
                .createdAt(AuditProvider.CREATED_AT)
                .updatedAt(AuditProvider.UPDATED_AT)
                .createdBy(AuditProvider.CREATED_BY)
                .updatedBy(AuditProvider.UPDATED_BY)
                .build();
    }

    public static OauthClientEntity otherOauthClientEntity() {
        return OauthClientEntity.builder()
                .id(OTHER_CLIENT_ID)
                .clientId(OTHER_CLIENT_CLIENT_ID)
                .clientSecret(OTHER_CLIENT_SECRET)
                .scopes(List.of(ScopeProvider.profileScopeEntity()))
                .redirectUris(List.of(RedirectUriProvider.otherRedirectUriEntity()))
                .grantTypes(List.of(GrantTypeProvider.otherGrantTypeEntity()))
                .createdAt(AuditProvider.CREATED_AT)
                .updatedAt(AuditProvider.UPDATED_AT)
                .createdBy(AuditProvider.CREATED_BY)
                .updatedBy(AuditProvider.UPDATED_BY)
                .build();
    }

    public static OauthClientDtoIn oauthClientDtoIn() {
        return OauthClientDtoIn.builder()
                .clientId(CLIENT_CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .scopeIds(List.of(ScopeProvider.OPENID_ID))
                .redirectUriIds(List.of(RedirectUriProvider.REDIRECT_URI_ID))
                .grantTypeIds(List.of(GrantTypeProvider.GRANT_TYPE_ID))
                .build();
    }

    public static OauthClientDtoIn otherOauthClientDtoIn() {
        return OauthClientDtoIn.builder()
                .clientId(OTHER_CLIENT_CLIENT_ID)
                .clientSecret(OTHER_CLIENT_SECRET)
                .scopeIds(List.of(ScopeProvider.PROFILE_ID))
                .redirectUriIds(List.of(RedirectUriProvider.OTHER_REDIRECT_URI_ID))
                .grantTypeIds(List.of(GrantTypeProvider.OTHER_GRANT_TYPE_ID))
                .build();
    }

    public static OauthClientDtoOut oauthClientDtoOut(Long id) {
        return OauthClientDtoOut.builder()
                .id(id)
                .clientId(CLIENT_CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .scopes(List.of(ScopeProvider.openidScopeDtoOut(ScopeProvider.OPENID_ID)))
                .redirectUris(List.of(RedirectUriProvider.redirectUriDtoOut(RedirectUriProvider.REDIRECT_URI_ID)))
                .grantTypes(List.of(GrantTypeProvider.grantTypeDtoOut(GrantTypeProvider.GRANT_TYPE_ID)))
                .createdAt(AuditProvider.CREATED_AT)
                .updatedAt(AuditProvider.UPDATED_AT)
                .createdBy(AuditProvider.CREATED_BY)
                .updatedBy(AuditProvider.UPDATED_BY)
                .build();
    }

    public static OauthClientDtoOut otherOauthClientDtoOut(Long id) {
        return OauthClientDtoOut.builder()
                .id(id)
                .clientId(OTHER_CLIENT_CLIENT_ID)
                .clientSecret(OTHER_CLIENT_SECRET)
                .scopes(List.of(ScopeProvider.profileScopeDtoOut(ScopeProvider.PROFILE_ID)))
                .redirectUris(
                        List.of(RedirectUriProvider.otherRedirectUriDtoOut(RedirectUriProvider.OTHER_REDIRECT_URI_ID)))
                .grantTypes(List.of(GrantTypeProvider.otherGrantTypeDtoOut(GrantTypeProvider.OTHER_GRANT_TYPE_ID)))
                .createdAt(AuditProvider.CREATED_AT)
                .updatedAt(AuditProvider.UPDATED_AT)
                .createdBy(AuditProvider.CREATED_BY)
                .updatedBy(AuditProvider.UPDATED_BY)
                .build();
    }
}
