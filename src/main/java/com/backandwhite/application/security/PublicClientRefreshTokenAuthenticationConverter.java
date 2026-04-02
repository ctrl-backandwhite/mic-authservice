package com.backandwhite.application.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.StringUtils;

/**
 * Handles refresh_token requests for public OAuth2 clients
 * (those using {@link ClientAuthenticationMethod#NONE}).
 * <p>
 * Spring Authorization Server's default
 * {@code PublicClientAuthenticationConverter}
 * only processes {@code authorization_code} requests. This converter fills the
 * gap
 * so public clients can also refresh their tokens.
 */
public class PublicClientRefreshTokenAuthenticationConverter implements AuthenticationConverter {

    @Override
    public Authentication convert(HttpServletRequest request) {
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!"refresh_token".equals(grantType)) {
            return null;
        }

        String clientId = request.getParameter(OAuth2ParameterNames.CLIENT_ID);
        if (!StringUtils.hasText(clientId)) {
            return null;
        }

        // If a client_secret is present this is NOT a public client
        String clientSecret = request.getParameter(OAuth2ParameterNames.CLIENT_SECRET);
        if (StringUtils.hasText(clientSecret)) {
            return null;
        }

        return new OAuth2ClientAuthenticationToken(
                clientId,
                ClientAuthenticationMethod.NONE,
                null,
                null);
    }
}
