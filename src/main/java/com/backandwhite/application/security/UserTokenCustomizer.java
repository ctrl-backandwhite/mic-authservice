package com.backandwhite.application.security;

import com.backandwhite.domain.model.Group;
import com.backandwhite.domain.model.User;
import com.backandwhite.domain.model.UserSession;
import com.backandwhite.domain.repository.GroupRepository;
import com.backandwhite.domain.repository.UserRepository;
import com.backandwhite.domain.repository.UserSessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Log4j2
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class UserTokenCustomizer {

    public static final String ID_TOKEN = "id_token";
    public static final String TOKEN_TYPE = "token_type";

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final UserSessionRepository userSessionRepository;

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {

        return context -> {
            context.getJwsHeader().algorithm(MacAlgorithm.HS256);
            Authentication principal = context.getPrincipal();
            if (context.getTokenType().getValue().equals(ID_TOKEN)) {
                context.getClaims().claim(TOKEN_TYPE, "id token");
            }
            if (context.getTokenType().getValue().equals("access_token")) {
                User user = userRepository.findUserByEmail(principal.getName());
                log.info("Información de usuario: {}", user);
                context.getClaims().claim(TOKEN_TYPE, "access token");
                List<String> roles = context.getPrincipal().getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(auth -> !auth.equals("FACTOR_PASSWORD"))
                        .toList();
                List<String> groups = getGetGroups(user.getGroups());
                context.getClaims().claim("id", user.getId());
                context.getClaims().claim("email", user.getEmail());
                context.getClaims().claim("firstName", user.getName());
                context.getClaims().claim("lastName", user.getLastName());
                context.getClaims().claim("roles", roles);
                context.getClaims().claim("status", user.getEnabled());
                context.getClaims().claim("groups", groups);

                // ── Session tracking ─────────────────────────────────────
                trackSession(context, user);
            }
        };
    }

    private void trackSession(JwtEncodingContext context, User user) {
        try {
            AuthorizationGrantType grantType = context.getAuthorizationGrantType();

            if (AuthorizationGrantType.AUTHORIZATION_CODE.equals(grantType)) {
                // New login → create session
                String sessionId = UUID.randomUUID().toString().replace("-", "");
                context.getClaims().claim("sid", sessionId);

                String authorizationId = context.getAuthorization() != null
                        ? context.getAuthorization().getId()
                        : null;

                HttpServletRequest request = getHttpRequest();
                String userAgent = request != null ? request.getHeader("User-Agent") : null;
                String ipAddress = request != null ? extractIpAddress(request) : null;

                UserSession session = UserSession.builder()
                        .userId(user.getId())
                        .sessionId(sessionId)
                        .authorizationId(authorizationId)
                        .deviceInfo(parseDeviceInfo(userAgent))
                        .ipAddress(ipAddress)
                        .userAgent(userAgent)
                        .createdAt(Instant.now())
                        .lastActiveAt(Instant.now())
                        .revoked(false)
                        .build();

                userSessionRepository.save(session);
                log.info("::> Session created: {} for user {}", sessionId, user.getEmail());

            } else if (AuthorizationGrantType.REFRESH_TOKEN.equals(grantType)) {
                // Token refresh → reuse existing session_id
                OAuth2Authorization auth = context.getAuthorization();
                String sessionId = null;

                if (auth != null && auth.getAccessToken() != null
                        && auth.getAccessToken().getClaims() != null) {
                    sessionId = (String) auth.getAccessToken().getClaims().get("sid");
                }

                if (sessionId != null) {
                    context.getClaims().claim("sid", sessionId);
                    userSessionRepository.updateLastActiveAt(sessionId);
                    log.debug("::> Session refreshed: {} for user {}", sessionId, user.getEmail());
                }
            }
        } catch (Exception e) {
            log.error("::> Failed to track session for user {}", user.getEmail(), e);
        }
    }

    private HttpServletRequest getHttpRequest() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attrs != null ? attrs.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String extractIpAddress(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String parseDeviceInfo(String userAgent) {
        if (userAgent == null || userAgent.isBlank())
            return "Navegador desconocido";

        String browser;
        if (userAgent.contains("Edg/"))
            browser = "Edge";
        else if (userAgent.contains("OPR/") || userAgent.contains("Opera"))
            browser = "Opera";
        else if (userAgent.contains("Chrome/") && !userAgent.contains("Edg/"))
            browser = "Chrome";
        else if (userAgent.contains("Firefox/"))
            browser = "Firefox";
        else if (userAgent.contains("Safari/") && !userAgent.contains("Chrome/"))
            browser = "Safari";
        else
            browser = "Navegador";

        String os;
        if (userAgent.contains("iPhone"))
            os = "iPhone";
        else if (userAgent.contains("iPad"))
            os = "iPad";
        else if (userAgent.contains("Android"))
            os = "Android";
        else if (userAgent.contains("Macintosh"))
            os = "macOS";
        else if (userAgent.contains("Windows"))
            os = "Windows";
        else if (userAgent.contains("Linux"))
            os = "Linux";
        else
            os = "Dispositivo";

        return browser + " · " + os;
    }

    private List<String> getGetGroups(List<Group> groups) {

        if (Objects.nonNull(groups) && !groups.isEmpty()) {
            return groups.stream()
                    .map(Group::getUniqueName)
                    .toList();
        }
        return Collections.emptyList();
    }
}
