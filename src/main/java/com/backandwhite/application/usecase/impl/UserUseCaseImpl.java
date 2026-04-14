package com.backandwhite.application.usecase.impl;

import com.backandwhite.application.handler.UserCommandHandler;
import com.backandwhite.application.mapper.UserUpdateMapper;
import com.backandwhite.application.port.out.AuthEventPort;
import com.backandwhite.application.port.out.NotificationEventPort;
import com.backandwhite.application.usecase.UserUseCase;
import com.backandwhite.common.exception.ArgumentException;
import com.backandwhite.core.kafka.avro.EmailNotificationEvent;
import com.backandwhite.domain.model.Role;
import com.backandwhite.domain.model.User;
import com.backandwhite.domain.model.UserSession;
import com.backandwhite.domain.repository.RoleRepository;
import com.backandwhite.domain.repository.UserRepository;
import com.backandwhite.domain.repository.UserSessionRepository;
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

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.backandwhite.common.exception.Message.ENTITY_NOT_FOUND;
import static com.backandwhite.common.exception.Message.VALIDATION_ERROR;

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

    @Value("${app.activation.base-url:http://localhost:6001}")
    private String activationBaseUrl;

    @Override
    @Transactional
    @CacheEvict(value = "user_all", allEntries = true)
    public User save(User model) {

        User existingUser = userRepository.findUserByEmail(model.getEmail());
        if (Objects.nonNull(existingUser)) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "Email already exists.");
        }

        if (model.getRoles() == null || model.getRoles().isEmpty()) {
            Role guestRole = findGuestRole();
            if (guestRole != null) {
                model.setRoles(new ArrayList<>(List.of(guestRole)));
                log.debug("::> Assigned default GUEST role to user {}", model.getEmail());
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

        log.debug("::> Creating user {} with activation token", model.getEmail());
        userCommandHandler.validate(model);
        User savedUser = userRepository.save(model);

        // Send activation email via Kafka
        sendActivationEmail(savedUser, activationToken);

        // Publish customer.registered event (M-13) — userdetailservice can auto-create
        // profile
        authEventPort.publishCustomerRegistered(savedUser.getId().toString(), savedUser.getEmail(), savedUser.getName(),
                savedUser.getLastName());

        return savedUser;
    }

    private void sendActivationEmail(User user, String activationToken) {
        String activationUrl = activationBaseUrl + "/api/v1/users/activate?token=" + activationToken;

        Map<String, String> variables = new HashMap<>();
        variables.put("name", user.getName());
        variables.put("activationUrl", activationUrl);

        EmailNotificationEvent event = EmailNotificationEvent.newBuilder().setRecipient(user.getEmail())
                .setSubject("Activate your account in Nexa").setTemplateName("account-activation")
                .setVariables(variables).build();

        notificationEventPort.sendNotificationEvent(event);
        log.debug("::> Activation email event sent for user {}", user.getEmail());
    }

    private Role findGuestRole() {
        try {
            List<Role> allRoles = roleRepository.findAll();
            return allRoles.stream().filter(role -> "ROLE_GUEST".equals(role.getUniqueName())).findFirst().orElse(null);
        } catch (Exception e) {
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
        User model = userRepository.getById(id);
        if (Objects.isNull(model)) {
            throw ENTITY_NOT_FOUND.toEntityNotFound("User", id);
        }
        return model;
    }

    @Override
    @Transactional
    @CachePut(value = "user", key = "#id") // actualiza cache individual
    @CacheEvict(value = "user_all", allEntries = true) // limpia cache de lista
    public User update(User model, Long id) {
        log.debug("::> Updating user {}", model);
        User existing = this.getById(id);
        userUpdateMapper.updateFromModel(model, existing);
        userCommandHandler.validate(existing);
        return userRepository.update(existing);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"user_all", "user"}, allEntries = true)
    public void delete(Long id) {
        this.getById(id);
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
        User existing = this.getById(id);
        existing.setEnabled(!Boolean.TRUE.equals(existing.getEnabled()));
        return userRepository.update(existing);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"user_all", "user"}, allEntries = true)
    public void activateUser(String token) {
        log.debug("::> Activating user with token: {}", token);
        User user = userRepository.findByActivationToken(token);
        if (user == null) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "Invalid or expired activation token.");
        }
        if (user.getActivationTokenExpiry() != null && Instant.now().isAfter(user.getActivationTokenExpiry())) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(),
                    "Activation token has expired. Please register again.");
        }
        if (Boolean.TRUE.equals(user.getEnabled())) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "User account is already activated.");
        }
        user.setEnabled(true);
        user.setActivationToken(null);
        user.setActivationTokenExpiry(null);
        log.debug("::> User {} activated successfully", user.getEmail());
        userRepository.update(user);
        sendWelcomeEmail(user);
    }

    private void sendWelcomeEmail(User user) {
        String loginUrl = activationBaseUrl + "/login";

        Map<String, String> variables = new HashMap<>();
        variables.put("name", user.getName());
        variables.put("loginUrl", loginUrl);

        EmailNotificationEvent event = EmailNotificationEvent.newBuilder().setRecipient(user.getEmail())
                .setSubject("Welcome to NX036!").setTemplateName("welcome-email").setVariables(variables).build();

        notificationEventPort.sendNotificationEvent(event);
        log.debug("::> Welcome email event sent for user {}", user.getEmail());
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        String normalizedEmail = email == null ? null : email.trim().toLowerCase();
        log.debug("::> Password reset requested for email: {}", normalizedEmail);
        User user = userRepository.findUserByEmail(normalizedEmail);
        if (user == null) {
            // Don't reveal if the email exists — just return silently
            log.warn("::> Password reset requested for non-existent email: {}", normalizedEmail);
            return;
        }
        if (!Boolean.TRUE.equals(user.getEnabled())) {
            log.warn("::> Password reset requested for disabled account: {}", normalizedEmail);
            return;
        }

        String resetToken = UUID.randomUUID().toString().replace("-", "");
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(Instant.now().plus(30, ChronoUnit.MINUTES));
        userRepository.update(user);

        sendPasswordResetEmail(user, resetToken);
        log.debug("::> Password reset token generated for user {}", email);
    }

    private void sendPasswordResetEmail(User user, String resetToken) {
        String resetUrl = activationBaseUrl + "/reset-password.html?token=" + resetToken;

        Map<String, String> variables = new HashMap<>();
        variables.put("name", user.getName());
        variables.put("resetUrl", resetUrl);

        EmailNotificationEvent event = EmailNotificationEvent.newBuilder().setRecipient(user.getEmail())
                .setSubject("Recover your password in Nexa").setTemplateName("password-reset").setVariables(variables)
                .build();

        notificationEventPort.sendNotificationEvent(event);
        log.debug("::> Password reset email event sent for user {}", user.getEmail());
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
        log.debug("::> Password reset successfully for user {}", user.getEmail());
    }

    // ─── Change-password flow (authenticated user) ────────────────────────

    @Override
    @Transactional
    public void requestPasswordChange(String email, String currentPassword, String newPassword,
                                      String confirmPassword) {
        log.debug("::> Password change requested for email: {}", email);
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "User not found.");
        }

        // 1. Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "The current password is incorrect.");
        }

        // 2. Verify new passwords match
        if (!newPassword.equals(confirmPassword)) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "The new passwords do not match.");
        }

        // 3. Generate unique 6-digit code
        String code = generateUniqueCode();

        // 4. Persist code with 3-minute expiry + store encoded new password temporarily
        user.setPasswordChangeCode(code);
        user.setPasswordChangeCodeExpiry(Instant.now().plus(3, ChronoUnit.MINUTES));
        // Store the encoded new password temporarily in passwordResetToken
        user.setPasswordResetToken(passwordEncoder.encode(newPassword));
        user.setPasswordResetTokenExpiry(null);
        userRepository.update(user);

        // 5. Send code via email
        sendPasswordChangeCodeEmail(user, code);
        log.debug("::> Password change code generated for user {}", email);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"user_all", "user"}, allEntries = true)
    public void confirmPasswordChange(String email, String code) {
        log.debug("::> Confirming password change for email: {}", email);
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "User not found.");
        }

        // Validate code
        if (user.getPasswordChangeCode() == null || !user.getPasswordChangeCode().equals(code.trim())) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "The verification code is invalid.");
        }

        // Validate expiry
        if (user.getPasswordChangeCodeExpiry() != null && Instant.now().isAfter(user.getPasswordChangeCodeExpiry())) {
            // Clear expired code
            user.setPasswordChangeCode(null);
            user.setPasswordChangeCodeExpiry(null);
            userRepository.update(user);
            throw new ArgumentException(VALIDATION_ERROR.getCode(),
                    "The verification code has expired. Please request a new one.");
        }

        // Code is valid — the new password was already validated and stored temporarily
        // We need the newPassword here. Let's store it encoded when requesting.
        // Actually, we should store the new password (encoded) at request time and
        // apply it on confirm.
        // But current flow: request stores code + we need newPassword at confirm.
        // Better approach: store newPassword (already encoded) along with code at
        // request time.
        // Let me refactor: store encoded new password in the password_reset_token field
        // temporarily.

        // Clear the code (one-time use)
        user.setPasswordChangeCode(null);
        user.setPasswordChangeCodeExpiry(null);

        // The new encoded password was stored in passwordResetToken during
        // requestPasswordChange
        if (user.getPasswordResetToken() != null && user.getPasswordResetToken().startsWith("$2a$")) {
            user.setPassword(user.getPasswordResetToken());
            user.setPasswordResetToken(null);
            user.setPasswordResetTokenExpiry(null);
        }

        userRepository.update(user);
        log.debug("::> Password changed successfully for user {}", email);
    }

    private String generateUniqueCode() {
        SecureRandom random = new SecureRandom();
        String code;
        int attempts = 0;
        do {
            code = String.format("%06d", random.nextInt(1_000_000));
            attempts++;
        } while (userRepository.findByPasswordChangeCode(code) != null && attempts < 10);
        return code;
    }

    private void sendPasswordChangeCodeEmail(User user, String code) {
        Map<String, String> variables = new HashMap<>();
        variables.put("name", user.getName());
        variables.put("code", code);

        EmailNotificationEvent event = EmailNotificationEvent.newBuilder().setRecipient(user.getEmail())
                .setSubject("Verification code for password change").setTemplateName("password-change-code")
                .setVariables(variables).build();

        notificationEventPort.sendNotificationEvent(event);
        log.debug("::> Password change code email sent for user {}", user.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "user", key = "#username.trim().toLowerCase()")
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        String normalized = username.trim().toLowerCase();
        log.debug("::> Loading user by identifier: {}", normalized);

        // Try email first, then nickName
        User user = userRepository.findUserByEmail(normalized);
        if (Objects.isNull(user)) {
            user = userRepository.findUserByNickName(normalized);
        }
        if (Objects.isNull(user)) {
            log.warn("::> User not found for identifier: {}", normalized);
            throw ENTITY_NOT_FOUND.toEntityNotFound("User", normalized);
        }
        log.debug("::> User loaded successfully: {} with {} roles", normalized,
                user.getRoles() != null ? user.getRoles().size() : 0);
        return user;
    }

    // ─── Session management ──────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<UserSession> getActiveSessions(String email) {
        log.debug("::> Getting active sessions for: {}", email);
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            return List.of();
        }
        return userSessionRepository.findActiveByUserId(user.getId());
    }

    @Override
    @Transactional
    public void requestSessionRevoke(String email, String sessionId) {
        log.debug("::> Session revoke requested for email: {}, session: {}", email, sessionId);
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "User not found.");
        }

        // Verify the session belongs to this user
        UserSession session = userSessionRepository.findBySessionId(sessionId);
        if (session == null || !session.getUserId().equals(user.getId())) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "Session not found.");
        }
        if (Boolean.TRUE.equals(session.getRevoked())) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "The session has already been closed.");
        }

        // Generate unique 6‐digit code
        String code = generateUniqueSessionRevokeCode();

        // Store code + expiry + target session
        user.setSessionRevokeCode(code);
        user.setSessionRevokeCodeExpiry(Instant.now().plus(3, ChronoUnit.MINUTES));
        user.setSessionToRevoke(sessionId);
        userRepository.update(user);

        // Send code via email
        sendSessionRevokeCodeEmail(user, code);
        log.debug("::> Session revoke code generated for user {}", email);
    }

    @Override
    @Transactional
    public void confirmSessionRevoke(String email, String code) {
        log.debug("::> Confirming session revoke for email: {}", email);
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "User not found.");
        }

        // Validate code
        if (user.getSessionRevokeCode() == null || !user.getSessionRevokeCode().equals(code.trim())) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "The verification code is invalid.");
        }

        // Validate expiry
        if (user.getSessionRevokeCodeExpiry() != null && Instant.now().isAfter(user.getSessionRevokeCodeExpiry())) {
            user.setSessionRevokeCode(null);
            user.setSessionRevokeCodeExpiry(null);
            user.setSessionToRevoke(null);
            userRepository.update(user);
            throw new ArgumentException(VALIDATION_ERROR.getCode(),
                    "The verification code has expired. Please request a new one.");
        }

        String sessionId = user.getSessionToRevoke();

        // Clear code fields
        user.setSessionRevokeCode(null);
        user.setSessionRevokeCodeExpiry(null);
        user.setSessionToRevoke(null);
        userRepository.update(user);

        if (sessionId != null) {
            // Mark session as revoked in DB
            UserSession session = userSessionRepository.findBySessionId(sessionId);
            if (session != null) {
                userSessionRepository.revokeSession(sessionId);

                // Try to remove OAuth2 authorization from in-memory service
                if (session.getAuthorizationId() != null) {
                    try {
                        OAuth2Authorization auth = authorizationService.findById(session.getAuthorizationId());
                        if (auth != null) {
                            authorizationService.remove(auth);
                            log.info("::> OAuth2 authorization revoked for session {}", sessionId);
                        }
                    } catch (Exception e) {
                        log.warn("::> Could not revoke OAuth2 authorization for session {}", sessionId, e);
                    }
                }
            }
            log.info("::> Session {} revoked for user {}", sessionId, email);
        }
    }

    private String generateUniqueSessionRevokeCode() {
        SecureRandom random = new SecureRandom();
        // Simple 6-digit code (no uniqueness check needed since it's per-user)
        return String.format("%06d", random.nextInt(1_000_000));
    }

    private void sendSessionRevokeCodeEmail(User user, String code) {
        Map<String, String> variables = new HashMap<>();
        variables.put("name", user.getName());
        variables.put("code", code);

        EmailNotificationEvent event = EmailNotificationEvent.newBuilder().setRecipient(user.getEmail())
                .setSubject("Verification code to close session").setTemplateName("session-revoke-code")
                .setVariables(variables).build();

        notificationEventPort.sendNotificationEvent(event);
        log.debug("::> Session revoke code email sent for user {}", user.getEmail());
    }
}
