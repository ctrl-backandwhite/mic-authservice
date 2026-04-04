package com.backandwhite.application.security;

import com.backandwhite.infrastructure.db.postgres.entity.UserSessionEntity;
import com.backandwhite.infrastructure.db.postgres.repository.UserSessionJpaRepositoryAdapter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor que valida que la sesión (claim "sid" del JWT) no haya sido revocada.
 * Si la sesión fue revocada, devuelve 401 inmediatamente para forzar el cierre
 * en el navegador del usuario.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class SessionValidationInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtDecoder jwtDecoder;
    private final UserSessionJpaRepositoryAdapter sessionJpaRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return true; // No JWT → allow (public endpoint)
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        String sessionId;
        try {
            Jwt jwt = jwtDecoder.decode(token);
            sessionId = jwt.getClaimAsString("sid");
        } catch (JwtException e) {
            log.debug("JWT decode failed in session interceptor: {}", e.getMessage());
            return true; // Let Spring Security handle invalid JWTs
        }

        if (sessionId == null || sessionId.isBlank()) {
            return true; // No session claim → allow (e.g. client_credentials tokens)
        }

        UserSessionEntity session = sessionJpaRepository.findBySessionId(sessionId);
        if (session != null && session.getRevoked()) {
            log.info("::> Blocked request from revoked session: {}", sessionId);

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    "{\"error\":\"session_revoked\",\"message\":\"Your session has been revoked\"}");
            return false;
        }

        return true;
    }
}
