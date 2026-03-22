package com.backandwhite.application.security;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.jackson.SecurityJacksonModules;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import tools.jackson.databind.json.JsonMapper;

import java.security.Principal;
import java.util.List;

/**
 * Servicio de autorización OAuth2 que persiste tokens en base de datos
 * y garantiza que cada usuario tenga como máximo un token activo por cliente.
 *
 * <p>
 * Antes de persistir, reemplaza el principal complejo del dominio
 * ({@code User}, {@code Role}, etc.) por un
 * {@link UsernamePasswordAuthenticationToken} ligero con solo el username
 * y {@link SimpleGrantedAuthority}. Así el serializador Jackson solo
 * maneja tipos estándar de Spring Security y no necesita un
 * {@code PolymorphicTypeValidator} personalizado.
 * </p>
 */
public class UniqueTokenOAuth2AuthorizationService implements OAuth2AuthorizationService {

    static final String DELETE_PREVIOUS_SQL = "DELETE FROM oauth2_authorization " +
            "WHERE registered_client_id = ? AND principal_name = ? AND id != ? " +
            "AND access_token_value IS NOT NULL";

    private final OAuth2AuthorizationService delegate;
    private final JdbcOperations jdbcOperations;

    public UniqueTokenOAuth2AuthorizationService(JdbcOperations jdbcOperations,
            RegisteredClientRepository registeredClientRepository) {
        JdbcOAuth2AuthorizationService jdbcService = new JdbcOAuth2AuthorizationService(jdbcOperations,
                registeredClientRepository);

        // Standard Security modules — no custom PTV needed because we
        // simplify the principal before persisting (see simplifyPrincipal).
        ClassLoader classLoader = JdbcOAuth2AuthorizationService.class.getClassLoader();
        List<tools.jackson.databind.JacksonModule> securityModules = SecurityJacksonModules.getModules(classLoader);

        JsonMapper jsonMapper = JsonMapper.builder()
                .addModules(securityModules)
                .build();

        jdbcService.setAuthorizationRowMapper(
                new JdbcOAuth2AuthorizationService.JsonMapperOAuth2AuthorizationRowMapper(
                        registeredClientRepository, jsonMapper));

        this.delegate = jdbcService;
        this.jdbcOperations = jdbcOperations;
    }

    // Constructor para tests — permite inyectar el delegate mockeado
    UniqueTokenOAuth2AuthorizationService(JdbcOperations jdbcOperations,
            OAuth2AuthorizationService delegate) {
        this.jdbcOperations = jdbcOperations;
        this.delegate = delegate;
    }

    @Override
    public void save(OAuth2Authorization authorization) {
        // Replace complex domain principal with lightweight standard types
        // before persisting, so Jackson never sees domain model classes.
        OAuth2Authorization simplified = simplifyPrincipal(authorization);

        if (simplified.getAccessToken() != null) {
            jdbcOperations.update(
                    DELETE_PREVIOUS_SQL,
                    simplified.getRegisteredClientId(),
                    simplified.getPrincipalName(),
                    simplified.getId());
        }
        delegate.save(simplified);
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

    // ------------------------------------------------------------------
    // Internal
    // ------------------------------------------------------------------

    /**
     * Replaces the {@link Authentication} stored in the authorization
     * attributes with a {@link UsernamePasswordAuthenticationToken} whose
     * principal is just the username {@link String} and whose authorities
     * are plain {@link SimpleGrantedAuthority} instances.
     *
     * <p>
     * This ensures the {@code oauth2_authorization.attributes} column
     * only contains standard Spring Security types that the default
     * {@link tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator}
     * already allows.
     * </p>
     */
    OAuth2Authorization simplifyPrincipal(OAuth2Authorization authorization) {
        Authentication auth = authorization.getAttribute(Principal.class.getName());

        if (auth == null || auth.getPrincipal() instanceof String) {
            return authorization; // already simple or absent
        }

        List<SimpleGrantedAuthority> authorities = auth.getAuthorities().stream()
                .map(a -> new SimpleGrantedAuthority(a.getAuthority()))
                .toList();

        UsernamePasswordAuthenticationToken simpleAuth = UsernamePasswordAuthenticationToken.authenticated(
                auth.getName(), null, authorities);

        return OAuth2Authorization.from(authorization)
                .attribute(Principal.class.getName(), simpleAuth)
                .build();
    }
}
