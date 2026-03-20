package com.backandwhite.application.security;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

/**
 * Servicio de autorización OAuth2 que persiste tokens en base de datos
 * y garantiza que cada usuario tenga como máximo un token activo por cliente.
 *
 * Al guardar una nueva autorización con access_token, elimina cualquier
 * autorización anterior del mismo usuario+cliente, evitando tokens huérfanos.
 */
public class UniqueTokenOAuth2AuthorizationService implements OAuth2AuthorizationService {

    static final String DELETE_PREVIOUS_SQL =
            "DELETE FROM oauth2_authorization " +
            "WHERE registered_client_id = ? AND principal_name = ? AND id != ? " +
            "AND access_token_value IS NOT NULL";

    private final OAuth2AuthorizationService delegate;
    private final JdbcOperations jdbcOperations;

    public UniqueTokenOAuth2AuthorizationService(JdbcOperations jdbcOperations,
                                                 RegisteredClientRepository registeredClientRepository) {
        this(jdbcOperations, new JdbcOAuth2AuthorizationService(jdbcOperations, registeredClientRepository));
    }

    // Constructor para tests — permite inyectar el delegate mockeado
    UniqueTokenOAuth2AuthorizationService(JdbcOperations jdbcOperations,
                                          OAuth2AuthorizationService delegate) {
        this.jdbcOperations = jdbcOperations;
        this.delegate = delegate;
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        if (authorization.getAccessToken() != null) {
            jdbcOperations.update(
                    DELETE_PREVIOUS_SQL,
                    authorization.getRegisteredClientId(),
                    authorization.getPrincipalName(),
                    authorization.getId()
            );
        }
        delegate.save(authorization);
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        delegate.remove(authorization);
    }

    @Override
    public OAuth2Authorization findById(String id) {
        return delegate.findById(id);
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        return delegate.findByToken(token, tokenType);
    }
}
