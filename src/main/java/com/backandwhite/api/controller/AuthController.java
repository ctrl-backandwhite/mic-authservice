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
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
public class AuthController {

    private final OAuth2AuthorizationService authorizationService;
    private final JwtDecoder jwtDecoder;
    private final UserSessionRepository userSessionRepository;

    private static final String BEARER_PREFIX = "Bearer ";

    @PostMapping("/logout")
    @Operation(summary = "Logout and revoke tokens", description = "Invalidates the user session and revokes all associated OAuth2 tokens")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(required = false) String token) {

        log.info("::> ========================================");
        log.info("::> Logout request received from: {}", request.getRemoteAddr());
        log.info("::> Session ID: {}",
                request.getSession(false) != null ? request.getSession(false).getId() : "No session");

        // ── Revoke active session using the JWT sid claim ──────────────
        revokeCurrentSession(request);

        // Get the current authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            log.info("::> Logging out user: {}", authentication.getName());

            // If a token is provided, revoke it
            if (token != null && !token.isEmpty()) {
                log.info("::> Revoking OAuth2 token");
                revokeToken(token);
            }
        }

        // Invalidate the HTTP session FIRST
        if (request.getSession(false) != null) {
            log.info("::> Invalidating HTTP session");
            request.getSession(false).invalidate();
        }

        // Use SecurityContextLogoutHandler to clean everything
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        // Clear the security context
        SecurityContextHolder.clearContext();

        // Delete cookies AFTER invalidating the session
        deleteCookies(request, response);

        log.info("::> User logged out successfully");
        log.info("::> ========================================");

        return ResponseEntity.noContent().build();
    }

    /**
     * Delete all session cookies
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

        // Explicitly delete JSESSIONID with multiple configurations
        // to ensure it is removed regardless of how it is configured
        log.info("::> Explicitly deleting JSESSIONID cookie");
        deleteCookie("JSESSIONID", request, response);

        log.info("::> Cookie deletion completed");
    }

    /**
     * Delete a specific cookie with multiple configurations
     */
    private void deleteCookie(String name, HttpServletRequest request, HttpServletResponse response) {
        // Configuration 1: Secure cookie with path /
        Cookie cookie1 = new Cookie(name, "");
        cookie1.setMaxAge(0);
        cookie1.setPath("/");
        cookie1.setHttpOnly(true);
        cookie1.setSecure(true);
        response.addCookie(cookie1);

        // Configuration 2: Non-secure cookie with path /
        Cookie cookie2 = new Cookie(name, "");
        cookie2.setMaxAge(0);
        cookie2.setPath("/");
        cookie2.setHttpOnly(true);
        cookie2.setSecure(false);
        response.addCookie(cookie2);

        // Configuration 3: Without specifying HttpOnly or Secure
        Cookie cookie3 = new Cookie(name, "");
        cookie3.setMaxAge(0);
        cookie3.setPath("/");
        response.addCookie(cookie3);

        log.info("::> Deleted cookie '{}' with multiple configurations", name);
    }

    @PostMapping("/revoke")
    @Operation(summary = "Revoke OAuth2 token", description = "Revokes a specific access token or refresh token")
    public ResponseEntity<Void> revokeToken(@RequestParam String token,
            @RequestParam(required = false, defaultValue = "access_token") String tokenTypeHint) {

        log.info("::> Token revocation request received");

        try {
            OAuth2TokenType tokenType = tokenTypeHint.equals("refresh_token")
                    ? OAuth2TokenType.REFRESH_TOKEN
                    : OAuth2TokenType.ACCESS_TOKEN;

            // Find the authorization by token
            OAuth2Authorization authorization = authorizationService.findByToken(token, tokenType);

            if (authorization != null) {
                log.info("::> Revoking token for client: {}", authorization.getRegisteredClientId());

                // Remove the authorization (this revokes the token)
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
            OAuth2Authorization authorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);

            if (authorization != null) {
                authorizationService.remove(authorization);
                log.info("::> Token revoked during logout");
            }
        } catch (Exception e) {
            log.error("::> Error revoking token during logout", e);
        }
    }

    /**
     * Extracts the session id (sid) from the JWT in the Authorization header and
     * revokes the session in the user_sessions table. Also removes the OAuth2
     * authorization from memory to invalidate the refresh token.
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

            // Revoke the session in the database
            userSessionRepository.revokeSession(sessionId);
            log.info("::> Session {} revoked during logout", sessionId);

            // Revoke the access token in the in-memory authorization service
            OAuth2Authorization authorization = authorizationService.findByToken(tokenValue,
                    OAuth2TokenType.ACCESS_TOKEN);
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
