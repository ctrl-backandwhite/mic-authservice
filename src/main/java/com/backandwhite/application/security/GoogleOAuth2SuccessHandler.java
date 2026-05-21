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
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * Handles successful OAuth2 authentication for every configured provider
 * (Google and X today). Auto-creates the user in the database if there is no
 * match for the provider's identifier yet. X's response has no email, so we
 * synthesise one from the username to keep the User schema unchanged.
 */
@Log4j2
public class GoogleOAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final String PROVIDER_GOOGLE = "google";
    private static final String PROVIDER_X = "x";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final NotificationEventPort notificationEventPort;
    private final AuthEventPort authEventPort;

    private final AuthNotificationHelper authNotificationHelper;

    public GoogleOAuth2SuccessHandler(UserRepository userRepository, RoleRepository roleRepository,
            NotificationEventPort notificationEventPort, AuthEventPort authEventPort) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.notificationEventPort = notificationEventPort;
        this.authEventPort = authEventPort;
        this.authNotificationHelper = new AuthNotificationHelper(notificationEventPort, userRepository);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String registrationId = (authentication instanceof OAuth2AuthenticationToken token)
                ? token.getAuthorizedClientRegistrationId()
                : PROVIDER_GOOGLE;

        if (PROVIDER_X.equalsIgnoreCase(registrationId)) {
            handleX(request, response, authentication, oAuth2User);
            return;
        }
        handleGoogle(request, response, authentication, oAuth2User);
    }

    /* ── Google ───────────────────────────────────────────────────────────── */

    private void handleGoogle(HttpServletRequest request, HttpServletResponse response, Authentication authentication,
            OAuth2User oAuth2User) throws IOException, ServletException {
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
                // Publish the email as the userId so the userdetailservice's
                // Kafka consumer ends up keyed by the same identifier the SPA
                // uses when hitting /profile/me (JWT sub = email for both
                // form-login and social-login). Using the numeric id would
                // create a duplicate user_profiles row that the storefront
                // never reads.
                authEventPort.publishCustomerRegistered(new CustomerRegisteredRequest(newUser.getEmail(),
                        newUser.getEmail(), newUser.getName(), newUser.getLastName()));
            } else {
                log.info("::> [GOOGLE-OAUTH2] Existing user login userId={}", existingUser.getId());
            }
            authNotificationHelper.sendAuthNotification(email, "New sign-in to your NX036 account", "login-success",
                    request);
        } catch (RuntimeException e) {
            log.error("::> [GOOGLE-OAUTH2] Registration failed reason={}", e.getMessage());
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

    /* ── X (Twitter) ──────────────────────────────────────────────────────── */

    private void handleX(HttpServletRequest request, HttpServletResponse response, Authentication authentication,
            OAuth2User oAuth2User) throws IOException, ServletException {
        // X's API v2 returns user data flattened by XOAuth2UserService:
        // id (numeric string), username (handle without @), name (display name)
        String xId = oAuth2User.getAttribute("id");
        String username = oAuth2User.getAttribute("username");
        String displayName = oAuth2User.getAttribute("name");

        if (xId == null || xId.isBlank()) {
            log.warn("::> [X-OAUTH2] Login failed: missing 'id' attribute");
            response.sendRedirect("/login?error=x_no_id");
            return;
        }

        // X does NOT expose user email under the default scope set. We mint a
        // stable synthetic email keyed by the X numeric id so we can identify
        // the same user across logins without adding a new DB column.
        String syntheticEmail = ("x-" + xId + "@x.local").toLowerCase();
        String name = parseFirstName(displayName, username);
        String lastName = parseLastName(displayName);
        String lang = extractLang(request);

        try {
            User existingUser = userRepository.findUserByEmail(syntheticEmail);
            if (existingUser == null) {
                User newUser = createXUser(syntheticEmail, username, name, lastName);
                log.info("::> [X-OAUTH2] New user registered userId={} username={} lang={}", newUser.getId(), username,
                        lang);
                // Publish the email as the userId so the userdetailservice's
                // Kafka consumer ends up keyed by the same identifier the SPA
                // uses when hitting /profile/me (JWT sub = email for both
                // form-login and social-login). Using the numeric id would
                // create a duplicate user_profiles row that the storefront
                // never reads.
                authEventPort.publishCustomerRegistered(new CustomerRegisteredRequest(newUser.getEmail(),
                        newUser.getEmail(), newUser.getName(), newUser.getLastName()));
                // No welcome email — the synthetic address won't deliver.
            } else {
                log.info("::> [X-OAUTH2] Existing user login userId={} username={}", existingUser.getId(), username);
            }
        } catch (RuntimeException e) {
            log.error("::> [X-OAUTH2] Registration failed reason={}", e.getMessage());
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private User createXUser(String syntheticEmail, String username, String firstName, String lastName) {
        User user = new User();
        user.setEmail(syntheticEmail);
        user.setName(firstName != null && !firstName.isBlank() ? firstName : "X");
        user.setLastName(lastName != null && !lastName.isBlank() ? lastName : "User");
        user.setNickName(username != null && !username.isBlank() ? username : "x-" + UUID.randomUUID());
        user.setPassword(UUID.randomUUID().toString().toUpperCase());
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);

        List<Role> roles = findDefaultRoles();
        if (!roles.isEmpty()) {
            user.setRoles(new ArrayList<>(roles));
        }
        return userRepository.save(user);
    }

    private String parseFirstName(String displayName, String fallback) {
        if (displayName == null || displayName.isBlank()) {
            return fallback != null ? fallback : "X";
        }
        return displayName.trim().split("\\s+")[0];
    }

    private String parseLastName(String displayName) {
        if (displayName == null || displayName.isBlank()) {
            return "";
        }
        String[] parts = displayName.trim().split("\\s+");
        if (parts.length <= 1) {
            return "";
        }
        return String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
    }

    /* ── Google user creation (unchanged) ─────────────────────────────────── */

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

        List<Role> roles = findDefaultRoles();
        if (!roles.isEmpty()) {
            user.setRoles(new ArrayList<>(roles));
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

    /**
     * Loads ROLE_USER + ROLE_GUEST so social-login accounts (Google, X) can hit the
     * same /api/v1/* endpoints that form-login users can, while still carrying the
     * broader GUEST grant for public-facing checks. Endpoints gated with
     * {@code @NxUser} need ROLE_USER; assigning only ROLE_GUEST blocked saving
     * payment methods, addresses, etc. for new signups.
     */
    private List<Role> findDefaultRoles() {
        try {
            List<Role> all = roleRepository.findAll();
            List<Role> picked = new ArrayList<>();
            for (Role r : all) {
                if ("ROLE_USER".equals(r.getUniqueName()) || "ROLE_GUEST".equals(r.getUniqueName())) {
                    picked.add(r);
                }
            }
            return picked;
        } catch (RuntimeException e) {
            log.warn("::> [OAUTH2] Could not fetch default roles", e);
            return new ArrayList<>();
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
