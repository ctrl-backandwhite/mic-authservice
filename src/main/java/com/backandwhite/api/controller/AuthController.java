package com.backandwhite.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
     * Elimina una cookie invalidando su Max-Age.
     * Se usa request.isSecure() para mantener la misma configuración con la que fue creada.
     */
    private void deleteCookie(String name, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        response.addCookie(cookie);
        log.debug("::> Deleted cookie '{}'", name);
    }

    @PostMapping("/revoke")
    @Operation(summary = "Revocar token OAuth2", description = "Revoca un access token o refresh token específico")
    public ResponseEntity<Void> revokeToken(
            @RequestParam String token,
            @RequestParam(required = false, defaultValue = "access_token") String tokenTypeHint) {

        log.info("::> Token revocation request received");

        if (!tokenTypeHint.equals("access_token") && !tokenTypeHint.equals("refresh_token")) {
            log.warn("::> Invalid tokenTypeHint: {}", tokenTypeHint);
            return ResponseEntity.badRequest().build();
        }

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
}
