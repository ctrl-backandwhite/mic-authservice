package com.backandwhite.application.security;

import com.backandwhite.domain.model.OauthClient;
import com.backandwhite.domain.repository.OauthClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

/**
 * Repositorio personalizado que mapea los clientes OAuth de las entidades
 * personalizadas
 * del proyecto a la interfaz RegisteredClientRepository de Spring OAuth2
 */
@RequiredArgsConstructor
public class CustomRegisteredClientRepository implements RegisteredClientRepository {

    private final OauthClientRepository oauthClientRepository;

    @Override
    public void save(RegisteredClient registeredClient) {
        // No implementar - los clientes se gestionan a través de las entidades
        // personalizadas
        throw new UnsupportedOperationException("Use OauthClientRepository to manage OAuth2 clients");
    }

    @Override
    public RegisteredClient findById(String id) {
        // The id stored in oauth2_authorization is the OauthClient's database ID (as
        // String).
        try {
            Long dbId = Long.valueOf(id);
            OauthClient oauthClient = oauthClientRepository.getById(dbId);
            if (oauthClient == null) {
                return null;
            }
            return mapToRegisteredClient(oauthClient);
        } catch (NumberFormatException e) {
            return null;
        } catch (Exception e) {
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
     * Mapea una entidad OauthClient a un RegisteredClient de Spring OAuth2
     */
    private RegisteredClient mapToRegisteredClient(OauthClient oauthClient) {
        RegisteredClient.Builder builder = RegisteredClient
                .withId(oauthClient.getId().toString())
                .clientId(oauthClient.getClientId())
                .clientSecret(oauthClient.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .clientName(oauthClient.getClientId());

        // Agregar grant types
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

        // Agregar redirect URIs
        if (oauthClient.getRedirectUris() != null) {
            oauthClient.getRedirectUris().forEach(redirectUri -> builder.redirectUri(redirectUri.getValue()));
        }

        // Agregar scopes
        if (oauthClient.getScopes() != null) {
            oauthClient.getScopes().forEach(scope -> builder.scope(scope.getUniqueName()));
        }

        // Configurar client settings
        builder.clientSettings(ClientSettings.builder()
                .requireProofKey(true)
                .requireAuthorizationConsent(false)
                .build());

        return builder.build();
    }
}
