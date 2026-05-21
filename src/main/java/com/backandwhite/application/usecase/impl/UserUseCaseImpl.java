package com.backandwhite.application.usecase.impl;

import static com.backandwhite.common.exception.Message.ENTITY_NOT_FOUND;
import static com.backandwhite.common.exception.Message.VALIDATION_ERROR;

import com.backandwhite.application.handler.UserCommandHandler;
import com.backandwhite.application.mapper.UserUpdateMapper;
import com.backandwhite.application.port.out.AuthEventPort;
import com.backandwhite.application.port.out.CustomerRegisteredRequest;
import com.backandwhite.application.port.out.EmailNotificationRequest;
import com.backandwhite.application.port.out.NotificationEventPort;
import com.backandwhite.application.usecase.UserUseCase;
import com.backandwhite.common.exception.ArgumentException;
import com.backandwhite.domain.model.Role;
import com.backandwhite.domain.model.User;
import com.backandwhite.domain.model.UserSession;
import com.backandwhite.domain.repository.RoleRepository;
import com.backandwhite.domain.repository.UserRepository;
import com.backandwhite.domain.repository.UserSessionRepository;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserUseCaseImpl implements UserUseCase, UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserCommandHandler userCommandHandler;
    private final NotificationEventPort notificationEventPort;
    private final AuthEventPort authEventPort;
    private final UserSessionRepository userSessionRepository;
    private final OAuth2AuthorizationService authorizationService;
    private final UserUpdateMapper userUpdateMapper;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Value("${app.activation.base-url:http://localhost:6001}")
    private String activationBaseUrl;

    @Override
    @Transactional
    @CacheEvict(value = "user_all", allEntries = true)
    public User save(User model) {
        return saveInternal(model, "es");
    }

    @Override
    @Transactional
    @CacheEvict(value = "user_all", allEntries = true)
    public User save(User model, String lang) {
        return saveInternal(model, lang);
    }

    /**
     * Internal save used by both public overloads. Bypasses {@code @Transactional}
     * AOP (Sonar S6809) — the calling public method already opened the transaction;
     * self-invocation would skip the proxy anyway.
     */
    private User saveInternal(User model, String lang) {
        User existingUser = userRepository.findUserByEmail(model.getEmail());
        if (Objects.nonNull(existingUser)) {
            log.warn("::> [USER] Registration failed: email already exists");
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "Email already exists.");
        }

        if (model.getRoles() == null || model.getRoles().isEmpty()) {
            Role guestRole = findGuestRole();
            if (guestRole != null) {
                model.setRoles(new ArrayList<>(List.of(guestRole)));
                log.debug("::> Assigned default GUEST role to user");
            }
        }

        model.setPassword(passwordEncoder.encode(model.getPassword()));

        model.setEnabled(false);
        model.setAccountNonExpired(true);
        model.setAccountNonLocked(true);
        model.setCredentialsNonExpired(true);

        String activationToken = UUID.randomUUID().toString().replace("-", "");
        model.setActivationToken(activationToken);
        model.setActivationTokenExpiry(Instant.now().plus(24, ChronoUnit.HOURS));

        log.debug("::> Creating user with activation token");
        userCommandHandler.validate(model);
        User savedUser = userRepository.save(model);

        sendActivationEmail(savedUser, activationToken, lang);

        // Key by email so userdetailservice consumes with the same userId
        // the SPA uses on /profile/me (JWT sub = email).
        authEventPort.publishCustomerRegistered(new CustomerRegisteredRequest(savedUser.getEmail(),
                savedUser.getEmail(), savedUser.getName(), savedUser.getLastName()));

        log.info("::> [USER] Registration completed userId={} lang={}", savedUser.getId(), lang);
        return savedUser;
    }

    private void sendActivationEmail(User user, String activationToken, String lang) {
        String activationUrl = activationBaseUrl + "/api/v1/users/activate?token=" + activationToken + "&lang=" + lang;

        Map<String, String> variables = new HashMap<>();
        variables.put("name", user.getName());
        variables.put("activationUrl", activationUrl);
        variables.put("lang", lang);

        notificationEventPort.sendNotificationEvent(new EmailNotificationRequest(user.getEmail(),
                "Activate your account in Nexa", "account-activation", variables));
        log.info("::> [NOTIFICATION] Sent template=account-activation userId={} lang={}", user.getId(), lang);
    }

    private Role findGuestRole() {
        try {
            List<Role> allRoles = roleRepository.findAll();
            return allRoles.stream().filter(role -> "ROLE_GUEST".equals(role.getUniqueName())).findFirst().orElse(null);
        } catch (RuntimeException e) {
            log.warn("::> Could not fetch GUEST role from repository", e);
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "user_all")
    public List<User> findAll() {
        log.debug("::> Getting all users");
        return userRepository.findAll();
    }

    @Override
    @Cacheable(value = "user", key = "#id")
    public User getById(Long id) {
        log.debug("::> Getting user with id {}", id);
        return getByIdInternal(id);
    }

    /**
     * Internal lookup used by other use-case methods. Bypasses {@code @Cacheable}
     * AOP (Sonar S6809) — self-invocation would skip the proxy anyway.
     */
    private User getByIdInternal(Long id) {
        User model = userRepository.getById(id);
        if (Objects.isNull(model)) {
            throw ENTITY_NOT_FOUND.toEntityNotFound("User", id);
        }
        return model;
    }

    @Override
    @Transactional
    @CachePut(value = "user", key = "#id")
    @CacheEvict(value = "user_all", allEntries = true)
    public User update(User model, Long id) {
        return updateInternal(model, id, "es");
    }

    @Override
    @Transactional
    @CachePut(value = "user", key = "#id")
    @CacheEvict(value = "user_all", allEntries = true)
    public User update(User model, Long id, String lang) {
        return updateInternal(model, id, lang);
    }

    /**
     * Internal update used by both public overloads. Bypasses
     * {@code @Transactional} AOP (Sonar S6809) — the calling public method already
     * opened the transaction.
     */
    private User updateInternal(User model, Long id, String lang) {
        log.debug("::> Updating user with id {}", id);
        User existing = getByIdInternal(id);
        userUpdateMapper.updateFromModel(model, existing);
        userCommandHandler.validate(existing);
        User updated = userRepository.update(existing);

        sendUserModificationNotification(updated, lang);
        return updated;
    }

    private void sendUserModificationNotification(User user, String lang) {
        Map<String, String> variables = new HashMap<>();
        variables.put("name", user.getName());
        variables.put("lang", lang);

        notificationEventPort.sendNotificationEvent(new EmailNotificationRequest(user.getEmail(),
                "Your account has been modified", "user-modification", variables));
        log.info("::> [NOTIFICATION] Sent template=user-modification userId={} lang={}", user.getId(), lang);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"user_all", "user"}, allEntries = true)
    public void delete(Long id) {
        getByIdInternal(id);
        log.debug("::> Deleting user with id {}", id);
        userRepository.delete(id);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"user_all", "user"}, allEntries = true)
    public void deleteAll(List<Long> ids) {
        log.debug("::> Bulk deleting users with ids {}", ids);
        userRepository.deleteAll(ids);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"user_all", "user"}, allEntries = true)
    public User toggleEnabled(Long id) {
        log.debug("::> Toggling enabled for user with id {}", id);
        User existing = getByIdInternal(id);
        existing.setEnabled(!Boolean.TRUE.equals(existing.getEnabled()));
        return userRepository.update(existing);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"user_all", "user"}, allEntries = true)
    public void activateUser(String token, String lang) {
        log.debug("::> Activating user with token");
        User user = userRepository.findByActivationToken(token);
        if (user == null) {
            log.warn("::> [USER] Activation failed: invalid or expired token");
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "Invalid or expired activation token.");
        }
        if (user.getActivationTokenExpiry() != null && Instant.now().isAfter(user.getActivationTokenExpiry())) {
            log.warn("::> [USER] Activation failed: token expired userId={}", user.getId());
            throw new ArgumentException(VALIDATION_ERROR.getCode(),
                    "Activation token has expired. Please register again.");
        }
        if (Boolean.TRUE.equals(user.getEnabled())) {
            log.warn("::> [USER] Activation failed: already activated userId={}", user.getId());
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "User account is already activated.");
        }
        user.setEnabled(true);
        user.setActivationToken(null);
        user.setActivationTokenExpiry(null);
        userRepository.update(user);
        sendWelcomeEmail(user, lang);
        log.info("::> [USER] Activation completed userId={} lang={}", user.getId(), lang);
    }

    private void sendWelcomeEmail(User user, String lang) {
        String loginUrl = activationBaseUrl + "/login";

        Map<String, String> variables = new HashMap<>();
        variables.put("name", user.getName());
        variables.put("loginUrl", loginUrl);
        variables.put("lang", lang);

        notificationEventPort.sendNotificationEvent(
                new EmailNotificationRequest(user.getEmail(), "Welcome to NX036!", "welcome-email", variables));
        log.info("::> [NOTIFICATION] Sent template=welcome-email userId={} lang={}", user.getId(), lang);
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email, String lang) {
        String normalizedEmail = email == null ? null : email.trim().toLowerCase();
        log.debug("::> Password reset requested");
        User user = userRepository.findUserByEmail(normalizedEmail);
        if (user == null) {
            log.warn("::> [USER] Password reset failed: email not found");
            return;
        }
        if (!Boolean.TRUE.equals(user.getEnabled())) {
            log.warn("::> [USER] Password reset failed: account disabled userId={}", user.getId());
            return;
        }

        String resetToken = UUID.randomUUID().toString().replace("-", "");
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(Instant.now().plus(30, ChronoUnit.MINUTES));
        userRepository.update(user);

        sendPasswordResetEmail(user, resetToken, lang);
        log.info("::> [USER] Password reset token generated userId={} lang={}", user.getId(), lang);
    }

    private void sendPasswordResetEmail(User user, String resetToken, String lang) {
        String resetUrl = activationBaseUrl + "/reset-password.html?token=" + resetToken;

        Map<String, String> variables = new HashMap<>();
        variables.put("name", user.getName());
        variables.put("resetUrl", resetUrl);
        variables.put("lang", lang);

        notificationEventPort.sendNotificationEvent(new EmailNotificationRequest(user.getEmail(),
                resolvePasswordResetSubject(lang), "password-reset", variables));
        log.info("::> [NOTIFICATION] Sent template=password-reset userId={} lang={}", user.getId(), lang);
    }

    private String resolvePasswordResetSubject(String lang) {
        String key = lang == null ? "es" : lang.toLowerCase();
        if (key.startsWith("en"))
            return "Reset your NX036 password";
        if (key.startsWith("pt"))
            return "Redefine a tua palavra-passe NX036";
        return "Restablece tu contraseña de NX036";
    }

    @Override
    @Transactional
    @CacheEvict(value = {"user_all", "user"}, allEntries = true)
    public void resetPassword(String token, String newPassword) {
        log.debug("::> Resetting password with token");
        User user = userRepository.findByPasswordResetToken(token);
        if (user == null) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(),
                    "The recovery link is invalid or has already been used.");
        }
        if (user.getPasswordResetTokenExpiry() != null && Instant.now().isAfter(user.getPasswordResetTokenExpiry())) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(),
                    "The recovery link has expired. Please request a new one.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.update(user);
        log.debug("::> Password reset successfully");
    }

    // ─── Change-password flow (authenticated user) ────────────────────────

    @Override
    @Transactional
    public void requestPasswordChange(String email, String currentPassword, String newPassword, String confirmPassword,
            String lang) {
        log.debug("::> Password change requested");
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            log.warn("::> [USER] Password change failed: user not found");
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "User not found.");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            log.warn("::> [USER] Password change failed: incorrect current password userId={}", user.getId());
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "The current password is incorrect.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "The new passwords do not match.");
        }

        String code = generateUniqueCode();

        user.setPasswordChangeCode(code);
        user.setPasswordChangeCodeExpiry(Instant.now().plus(3, ChronoUnit.MINUTES));
        user.setPasswordResetToken(passwordEncoder.encode(newPassword));
        user.setPasswordResetTokenExpiry(null);
        userRepository.update(user);

        sendPasswordChangeCodeEmail(user, code, lang);
        log.info("::> [USER] Password change code generated userId={} lang={}", user.getId(), lang);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"user_all", "user"}, allEntries = true)
    public void confirmPasswordChange(String email, String code) {
        log.debug("::> Confirming password change");
        User user = findUserByEmailOrThrow(email);
        validateVerificationCode(user.getPasswordChangeCode(), code);
        validateCodeExpiry(user.getPasswordChangeCodeExpiry(), user, true);

        // Clear the code (one-time use)
        user.setPasswordChangeCode(null);
        user.setPasswordChangeCodeExpiry(null);

        // Apply the pre-encoded password stored during requestPasswordChange
        applyStoredPassword(user);

        userRepository.update(user);
        log.debug("::> Password changed successfully");
    }

    private User findUserByEmailOrThrow(String email) {
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "User not found.");
        }
        return user;
    }

    private void validateVerificationCode(String storedCode, String providedCode) {
        if (storedCode == null || !storedCode.equals(providedCode.trim())) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "The verification code is invalid.");
        }
    }

    private void validateCodeExpiry(Instant expiry, User user, boolean isPasswordChange) {
        if (expiry != null && Instant.now().isAfter(expiry)) {
            clearExpiredCode(user, isPasswordChange);
            userRepository.update(user);
            throw new ArgumentException(VALIDATION_ERROR.getCode(),
                    "The verification code has expired. Please request a new one.");
        }
    }

    private void clearExpiredCode(User user, boolean isPasswordChange) {
        if (isPasswordChange) {
            user.setPasswordChangeCode(null);
            user.setPasswordChangeCodeExpiry(null);
        } else {
            user.setSessionRevokeCode(null);
            user.setSessionRevokeCodeExpiry(null);
            user.setSessionToRevoke(null);
        }
    }

    private void applyStoredPassword(User user) {
        if (user.getPasswordResetToken() != null && user.getPasswordResetToken().startsWith("$2a$")) {
            user.setPassword(user.getPasswordResetToken());
            user.setPasswordResetToken(null);
            user.setPasswordResetTokenExpiry(null);
        }
    }

    private String generateUniqueCode() {
        String code;
        int attempts = 0;
        do {
            code = String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
            attempts++;
        } while (userRepository.findByPasswordChangeCode(code) != null && attempts < 10);
        return code;
    }

    private void sendPasswordChangeCodeEmail(User user, String code, String lang) {
        Map<String, String> variables = new HashMap<>();
        variables.put("name", user.getName());
        variables.put("code", code);
        variables.put("lang", lang);

        notificationEventPort.sendNotificationEvent(new EmailNotificationRequest(user.getEmail(),
                "Verification code for password change", "password-change-code", variables));
        log.info("::> [NOTIFICATION] Sent template=password-change-code userId={} lang={}", user.getId(), lang);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "user", key = "#username.trim().toLowerCase()")
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        String normalized = username.trim().toLowerCase();
        log.debug("::> Loading user by identifier");

        // Try email first, then nickName
        User user = userRepository.findUserByEmail(normalized);
        if (Objects.isNull(user)) {
            user = userRepository.findUserByNickName(normalized);
        }
        if (Objects.isNull(user)) {
            log.warn("::> User not found for identifier");
            throw ENTITY_NOT_FOUND.toEntityNotFound("User", normalized);
        }
        log.debug("::> User loaded successfully with {} roles", user.getRoles() != null ? user.getRoles().size() : 0);
        return user;
    }

    // ─── Session management ──────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<UserSession> getActiveSessions(String email) {
        log.debug("::> Getting active sessions");
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            return List.of();
        }
        return userSessionRepository.findActiveByUserId(user.getId());
    }

    @Override
    @Transactional
    public void requestSessionRevoke(String email, String sessionId, String lang) {
        log.debug("::> Session revoke requested");
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            log.warn("::> [USER] Session revoke failed: user not found");
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "User not found.");
        }

        UserSession session = userSessionRepository.findBySessionId(sessionId);
        if (session == null || !session.getUserId().equals(user.getId())) {
            log.warn("::> [USER] Session revoke failed: session not found userId={}", user.getId());
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "Session not found.");
        }
        if (Boolean.TRUE.equals(session.getRevoked())) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "The session has already been closed.");
        }

        String code = generateUniqueSessionRevokeCode();

        user.setSessionRevokeCode(code);
        user.setSessionRevokeCodeExpiry(Instant.now().plus(3, ChronoUnit.MINUTES));
        user.setSessionToRevoke(sessionId);
        userRepository.update(user);

        sendSessionRevokeCodeEmail(user, code, lang);
        log.info("::> [USER] Session revoke code generated userId={} lang={}", user.getId(), lang);
    }

    @Override
    @Transactional
    public void confirmSessionRevoke(String email, String code) {
        log.debug("::> Confirming session revoke");
        User user = findUserByEmailOrThrow(email);
        validateVerificationCode(user.getSessionRevokeCode(), code);
        validateCodeExpiry(user.getSessionRevokeCodeExpiry(), user, false);

        String sessionId = user.getSessionToRevoke();

        // Clear code fields
        user.setSessionRevokeCode(null);
        user.setSessionRevokeCodeExpiry(null);
        user.setSessionToRevoke(null);
        userRepository.update(user);

        revokeSessionById(sessionId);
    }

    private void revokeSessionById(String sessionId) {
        if (sessionId == null) {
            return;
        }
        UserSession session = userSessionRepository.findBySessionId(sessionId);
        if (session == null) {
            return;
        }
        userSessionRepository.revokeSession(sessionId);
        revokeOAuth2Authorization(session.getAuthorizationId(), sessionId);
        log.info("::> Session revoked for user");
    }

    private void revokeOAuth2Authorization(String authorizationId, String sessionId) {
        if (authorizationId == null) {
            return;
        }
        try {
            OAuth2Authorization auth = authorizationService.findById(authorizationId);
            if (auth != null) {
                authorizationService.remove(auth);
                log.info("::> OAuth2 authorization revoked for session");
            }
        } catch (RuntimeException e) {
            log.warn("::> Could not revoke OAuth2 authorization for session {}", sessionId, e);
        }
    }

    private String generateUniqueSessionRevokeCode() {
        // Simple 6-digit code (no uniqueness check needed since it's per-user)
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }

    private void sendSessionRevokeCodeEmail(User user, String code, String lang) {
        Map<String, String> variables = new HashMap<>();
        variables.put("name", user.getName());
        variables.put("code", code);
        variables.put("lang", lang);

        notificationEventPort.sendNotificationEvent(new EmailNotificationRequest(user.getEmail(),
                "Verification code to close session", "session-revoke-code", variables));
        log.info("::> [NOTIFICATION] Sent template=session-revoke-code userId={} lang={}", user.getId(), lang);
    }
}
