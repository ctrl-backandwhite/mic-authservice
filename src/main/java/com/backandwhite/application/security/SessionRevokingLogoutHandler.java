package com.backandwhite.application.security;

import com.backandwhite.domain.repository.UserSessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

/**
 * Marks the user_sessions row as revoked = true when the user logs out,
 * so it no longer appears in the "active sessions" list.
 */
@Log4j2
@Component
public class SessionRevokingLogoutHandler implements LogoutHandler {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtDecoder jwtDecoder;
    private final UserSessionRepository userSessionRepository;

    public SessionRevokingLogoutHandler(@Lazy JwtDecoder jwtDecoder,
            UserSessionRepository userSessionRepository) {
        this.jwtDecoder = jwtDecoder;
        this.userSessionRepository = userSessionRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.debug("::> Logout without Bearer token – skipping session revocation");
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        try {
            Jwt jwt = jwtDecoder.decode(token);
            String sessionId = jwt.getClaimAsString("sid");
            if (sessionId != null && !sessionId.isBlank()) {
                userSessionRepository.revokeSession(sessionId);
                log.info("::> Revoked session {} on logout", sessionId);
            }
        } catch (JwtException e) {
            log.debug("::> JWT decode failed during logout: {}", e.getMessage());
        }
    }
}
