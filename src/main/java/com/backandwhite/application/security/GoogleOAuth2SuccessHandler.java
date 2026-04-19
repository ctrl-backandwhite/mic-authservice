package com.backandwhite.application.security;

import com.backandwhite.application.port.out.AuthEventPort;
import com.backandwhite.application.port.out.CustomerRegisteredRequest;
import com.backandwhite.application.port.out.EmailNotificationRequest;
import com.backandwhite.application.port.out.NotificationEventPort;
import com.backandwhite.domain.model.Role;
import com.backandwhite.domain.model.User;
import com.backandwhite.domain.repository.RoleRepository;
import com.backandwhite.domain.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * Handles successful Google OAuth2 authentication. Auto-creates the user in the
 * database if the email does not already exist.
 */
@Log4j2
public class GoogleOAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final NotificationEventPort notificationEventPort;
    private final AuthEventPort authEventPort;

    public GoogleOAuth2SuccessHandler(UserRepository userRepository, RoleRepository roleRepository,
            NotificationEventPort notificationEventPort, AuthEventPort authEventPort) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.notificationEventPort = notificationEventPort;
        this.authEventPort = authEventPort;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String givenName = oAuth2User.getAttribute("given_name");
        String familyName = oAuth2User.getAttribute("family_name");

        if (email == null || email.isBlank()) {
            log.warn("::> [GOOGLE-OAUTH2] Login failed: no email in OAuth2 response");
            response.sendRedirect("/login?error=google_no_email");
            return;
        }

        String lang = extractLang(request);

        try {
            User existingUser = userRepository.findUserByEmail(email.trim().toLowerCase());

            if (existingUser == null) {
                User newUser = createGoogleUser(email, givenName, familyName);
                log.info("::> [GOOGLE-OAUTH2] New user registered userId={} lang={}", newUser.getId(), lang);

                sendWelcomeEmail(newUser, lang);

                authEventPort.publishCustomerRegistered(new CustomerRegisteredRequest(newUser.getId().toString(),
                        newUser.getEmail(), newUser.getName(), newUser.getLastName()));
            } else {
                log.info("::> [GOOGLE-OAUTH2] Existing user login userId={}", existingUser.getId());
            }
        } catch (RuntimeException e) {
            log.error("::> [GOOGLE-OAUTH2] Registration failed reason={}", e.getMessage());
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private User createGoogleUser(String email, String givenName, String familyName) {
        User user = new User();
        user.setEmail(email.trim().toLowerCase());
        user.setName(givenName != null ? givenName : "Google");
        user.setLastName(familyName != null ? familyName : "User");
        user.setNickName(email.split("@")[0]);
        user.setPassword(UUID.randomUUID().toString().toUpperCase());
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);

        Role guestRole = findGuestRole();
        if (guestRole != null) {
            user.setRoles(new ArrayList<>(List.of(guestRole)));
        }

        return userRepository.save(user);
    }

    private Role findGuestRole() {
        try {
            return roleRepository.findAll().stream().filter(role -> "ROLE_GUEST".equals(role.getUniqueName()))
                    .findFirst().orElse(null);
        } catch (RuntimeException e) {
            log.warn("::> [GOOGLE-OAUTH2] Could not fetch GUEST role", e);
            return null;
        }
    }

    private void sendWelcomeEmail(User user, String lang) {
        try {
            Map<String, String> variables = new HashMap<>();
            variables.put("name", user.getName());
            variables.put("lang", lang);

            notificationEventPort.sendNotificationEvent(
                    new EmailNotificationRequest(user.getEmail(), "Welcome to NX036!", "welcome-email", variables));
            log.info("::> [NOTIFICATION] Sent template=welcome-email userId={} lang={}", user.getId(), lang);
        } catch (RuntimeException e) {
            log.warn("::> [GOOGLE-OAUTH2] Welcome email failed reason={}", e.getMessage());
        }
    }

    private String extractLang(HttpServletRequest request) {
        String acceptLanguage = request.getHeader("Accept-Language");
        if (acceptLanguage != null && !acceptLanguage.isBlank()) {
            String primary = acceptLanguage.split(",")[0].trim().split(";")[0].trim().toLowerCase();
            if (primary.startsWith("en")) {
                return "en";
            }
            if (primary.startsWith("pt")) {
                return "pt";
            }
            if (primary.startsWith("es")) {
                return "es";
            }
        }
        return "es";
    }
}
