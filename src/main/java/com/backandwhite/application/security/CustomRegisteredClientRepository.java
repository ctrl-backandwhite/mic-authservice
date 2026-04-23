package com.backandwhite.application.security;

import com.backandwhite.domain.model.OauthClient;
import com.backandwhite.domain.repository.OauthClientRepository;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

/**
 * Custom repository that maps OAuth clients from the project's custom entities
 * to the RegisteredClientRepository interface of Spring OAuth2
 */
@RequiredArgsConstructor
public class CustomRegisteredClientRepository implements RegisteredClientRepository {

    private final OauthClientRepository oauthClientRepository;

    @Override
    public void save(RegisteredClient registeredClient) {
        // Not implemented - clients are managed through custom entities
        throw new UnsupportedOperationException("Use OauthClientRepository to manage OAuth2 clients");
    }

    @Override
    public RegisteredClient findById(String id) {
        try {
            OauthClient oauthClient = oauthClientRepository.getById(Long.parseLong(id));
            if (oauthClient == null) {
                return null;
            }
            return mapToRegisteredClient(oauthClient);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        OauthClient oauthClient = oauthClientRepository.findByClientId(clientId);
        if (oauthClient == null) {
            return null;
        }
        return mapToRegisteredClient(oauthClient);
    }

    /**
     * Maps an OauthClient entity to a Spring OAuth2 RegisteredClient
     */
    private RegisteredClient mapToRegisteredClient(OauthClient oauthClient) {
        RegisteredClient.Builder builder = RegisteredClient.withId(String.valueOf(oauthClient.getId()))
                .clientId(oauthClient.getClientId()).clientSecret(oauthClient.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE).clientName(oauthClient.getClientId());

        // Add grant types
        if (oauthClient.getGrantTypes() != null) {
            oauthClient.getGrantTypes().forEach(grantType -> {
                if ("authorization_code".equalsIgnoreCase(grantType.getValue())) {
                    builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
                } else if ("refresh_token".equalsIgnoreCase(grantType.getValue())) {
                    builder.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN);
                } else if ("client_credentials".equalsIgnoreCase(grantType.getValue())) {
                    builder.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS);
                }
            });
        }

        // Add redirect URIs
        if (oauthClient.getRedirectUris() != null) {
            oauthClient.getRedirectUris().forEach(redirectUri -> builder.redirectUri(redirectUri.getValue()));
        }

        // Add scopes
        if (oauthClient.getScopes() != null) {
            oauthClient.getScopes().forEach(scope -> builder.scope(scope.getUniqueName()));
        }

        // Configure client settings
        builder.clientSettings(
                ClientSettings.builder().requireProofKey(true).requireAuthorizationConsent(false).build());

        // Override Spring Authorization Server defaults (5 min access / 60 min
        // refresh). With the default a long-running admin job (bulk CJ sync,
        // discover) outlives the refresh token and gets bounced through OAuth
        // mid-work. 24 h access + 7 d refresh lets admins work a full day
        // without silent refresh churn while still keeping a weekly ceiling.
        // reuseRefreshTokens keeps the refresh token stable so the SPA
        // doesn't have to re-save it after every silent refresh.
        builder.tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofHours(24))
                .refreshTokenTimeToLive(Duration.ofDays(7)).reuseRefreshTokens(true).build());

        return builder.build();
    }
}
