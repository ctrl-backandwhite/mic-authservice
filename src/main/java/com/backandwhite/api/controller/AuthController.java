package com.backandwhite.api.controller;

import com.backandwhite.domain.repository.UserSessionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints de autenticación y autorización")
public class AuthController {

    private final OAuth2AuthorizationService authorizationService;
    private final JwtDecoder jwtDecoder;
    private final UserSessionRepository userSessionRepository;

    private static final String BEARER_PREFIX = "Bearer ";

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión y revocar tokens", description = "Invalida la sesión del usuario y revoca todos los tokens OAuth2 asociados")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false) String token) {

        log.info("::> ========================================");
        log.info("::> Logout request received from: {}", request.getRemoteAddr());
        log.info("::> Session ID: {}",
                request.getSession(false) != null ? request.getSession(false).getId() : "No session");

        // ── Revocar sesión activa usando el sid del JWT ──────────────
        revokeCurrentSession(request);

        // Obtener la autenticación actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            log.info("::> Logging out user: {}", authentication.getName());

            // Si se proporciona un token, revocarlo
            if (token != null && !token.isEmpty()) {
                log.info("::> Revoking OAuth2 token");
                revokeToken(token);
            }
        }

        // Invalidar la sesión HTTP PRIMERO
        if (request.getSession(false) != null) {
            log.info("::> Invalidating HTTP session");
            request.getSession(false).invalidate();
        }

        // Usar SecurityContextLogoutHandler para limpiar todo
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        // Limpiar el contexto de seguridad
        SecurityContextHolder.clearContext();

        // Eliminar cookies DESPUÉS de invalidar la sesión
        deleteCookies(request, response);

        log.info("::> User logged out successfully");
        log.info("::> ========================================");

        return ResponseEntity.noContent().build();
    }

    /**
     * Eliminar todas las cookies de sesión
     */
    private void deleteCookies(HttpServletRequest request, HttpServletResponse response) {
        log.info("::> Starting cookie deletion process");
        log.info("::> Request scheme: {}, isSecure: {}", request.getScheme(), request.isSecure());

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info("::> Processing cookie: {} = {}", cookie.getName(), cookie.getValue());
                deleteCookie(cookie.getName(), request, response);
            }
        }

        // Eliminar JSESSIONID explícitamente con múltiples configuraciones
        // para asegurar que se elimine independientemente de cómo esté configurada
        log.info("::> Explicitly deleting JSESSIONID cookie");
        deleteCookie("JSESSIONID", request, response);

        log.info("::> Cookie deletion completed");
    }

    /**
     * Eliminar una cookie específica con múltiples configuraciones
     */
    private void deleteCookie(String name, HttpServletRequest request, HttpServletResponse response) {
        // Configuración 1: Cookie segura con path /
        Cookie cookie1 = new Cookie(name, "");
        cookie1.setMaxAge(0);
        cookie1.setPath("/");
        cookie1.setHttpOnly(true);
        cookie1.setSecure(true);
        response.addCookie(cookie1);

        // Configuración 2: Cookie no segura con path /
        Cookie cookie2 = new Cookie(name, "");
        cookie2.setMaxAge(0);
        cookie2.setPath("/");
        cookie2.setHttpOnly(true);
        cookie2.setSecure(false);
        response.addCookie(cookie2);

        // Configuración 3: Sin especificar HttpOnly ni Secure
        Cookie cookie3 = new Cookie(name, "");
        cookie3.setMaxAge(0);
        cookie3.setPath("/");
        response.addCookie(cookie3);

        log.info("::> Deleted cookie '{}' with multiple configurations", name);
    }

    @PostMapping("/revoke")
    @Operation(summary = "Revocar token OAuth2", description = "Revoca un access token o refresh token específico")
    public ResponseEntity<Void> revokeToken(
            @RequestParam String token,
            @RequestParam(required = false, defaultValue = "access_token") String tokenTypeHint) {

        log.info("::> Token revocation request received");

        try {
            OAuth2TokenType tokenType = tokenTypeHint.equals("refresh_token")
                    ? OAuth2TokenType.REFRESH_TOKEN
                    : OAuth2TokenType.ACCESS_TOKEN;

            // Buscar la autorización por el token
            OAuth2Authorization authorization = authorizationService.findByToken(token, tokenType);

            if (authorization != null) {
                log.info("::> Revoking token for client: {}", authorization.getRegisteredClientId());

                // Remover la autorización (esto revoca el token)
                authorizationService.remove(authorization);

                log.info("::> Token revoked successfully");
                return ResponseEntity.noContent().build();
            } else {
                log.warn("::> Token not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("::> Error revoking token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void revokeToken(String token) {
        try {
            OAuth2Authorization authorization = authorizationService.findByToken(
                    token,
                    OAuth2TokenType.ACCESS_TOKEN);

            if (authorization != null) {
                authorizationService.remove(authorization);
                log.info("::> Token revoked during logout");
            }
        } catch (Exception e) {
            log.error("::> Error revoking token during logout", e);
        }
    }

    /**
     * Extrae el session id (sid) del JWT en el header Authorization y revoca
     * la sesión en la tabla user_sessions. También elimina la autorización
     * OAuth2 de la memoria para invalidar el refresh token.
     */
    private void revokeCurrentSession(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                log.debug("::> No Bearer token in logout request — skipping session revocation");
                return;
            }

            String tokenValue = authHeader.substring(BEARER_PREFIX.length());
            Jwt jwt = jwtDecoder.decode(tokenValue);
            String sessionId = jwt.getClaimAsString("sid");

            if (sessionId == null || sessionId.isBlank()) {
                log.debug("::> No sid claim in JWT — skipping session revocation");
                return;
            }

            // Revocar la sesión en la base de datos
            userSessionRepository.revokeSession(sessionId);
            log.info("::> Session {} revoked during logout", sessionId);

            // Revocar el access token en el in-memory authorization service
            OAuth2Authorization authorization = authorizationService.findByToken(
                    tokenValue, OAuth2TokenType.ACCESS_TOKEN);
            if (authorization != null) {
                authorizationService.remove(authorization);
                log.info("::> OAuth2 authorization removed for session {}", sessionId);
            }
        } catch (JwtException e) {
            log.debug("::> JWT decode failed during logout: {}", e.getMessage());
        } catch (Exception e) {
            log.error("::> Error revoking session during logout", e);
        }
    }
}
