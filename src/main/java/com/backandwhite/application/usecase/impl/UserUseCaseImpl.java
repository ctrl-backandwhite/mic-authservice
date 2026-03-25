package com.backandwhite.application.usecase.impl;

import com.backandwhite.application.service.NotificationProducerService;
import com.backandwhite.core.kafka.avro.EmailNotificationEvent;
import com.backandwhite.application.usecase.UserUseCase;
import com.backandwhite.common.exception.ArgumentException;
import com.backandwhite.domain.model.User;
import com.backandwhite.domain.model.Role;
import com.backandwhite.domain.repository.UserRepository;
import com.backandwhite.domain.repository.RoleRepository;

import com.backandwhite.application.handler.UserCommandHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final Optional<NotificationProducerService> notificationProducerService;

    @Value("${app.activation.base-url:http://localhost:6001}")
    private String activationBaseUrl;

    @Override
    @Transactional
    @CacheEvict(value = "user_all", allEntries = true)
    public User save(User model) {
        User existingUser = userRepository.findUserByEmail(model.getEmail());
        if (existingUser != null) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(), "Email already exists.");
        }

        // Assign default GUEST role if user has no roles
        if (model.getRoles() == null || model.getRoles().isEmpty()) {
            Role guestRole = findGuestRole();
            if (guestRole != null) {
                model.setRoles(new ArrayList<>(List.of(guestRole)));
                log.debug("::> Assigned default GUEST role to user {}", model.getEmail());
            }
        }

        model.setPassword(passwordEncoder.encode(model.getPassword()));

        // User starts disabled until email activation
        model.setEnabled(false);
        model.setAccountNonExpired(true);
        model.setAccountNonLocked(true);
        model.setCredentialsNonExpired(true);

        // Generate activation token with 24h expiry
        String activationToken = UUID.randomUUID().toString().replace("-", "");
        model.setActivationToken(activationToken);
        model.setActivationTokenExpiry(Instant.now().plus(24, ChronoUnit.HOURS));

        log.debug("::> Creating user {} with activation token", model.getEmail());
        userCommandHandler.validate(model);
        User savedUser = userRepository.save(model);

        // Send activation email via Kafka
        sendActivationEmail(savedUser, activationToken, "es");

        return savedUser;
    }

    @Override
    @Transactional
    @CacheEvict(value = "user_all", allEntries = true)
    public User save(User model, String lang) {
        User existingUser = userRepository.findUserByEmail(model.getEmail());
        if (existingUser != null) {
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

        sendActivationEmail(savedUser, activationToken, lang != null ? lang : "es");

        return savedUser;
    }

    private void sendActivationEmail(User user, String activationToken, String lang) {
        notificationProducerService.ifPresent(producer -> {
            String activationUrl = activationBaseUrl + "/api/v1/users/activate?token=" + activationToken;

            Map<String, String> variables = new HashMap<>();
            variables.put("name", user.getName());
            variables.put("activationUrl", activationUrl);
            variables.put("lang", lang);

            String subject = "en".equals(lang)
                    ? "Activate your account on NEXA"
                    : "Activa tu cuenta en NEXA";

            EmailNotificationEvent event = EmailNotificationEvent.newBuilder()
                    .setRecipient(user.getEmail())
                    .setSubject(subject)
                    .setTemplateName("account-activation")
                    .setVariables(variables)
                    .build();

            producer.sendNotificationEvent(event);
            log.debug("::> Activation email event sent for user {}", user.getEmail());
        });
    }

    private Role findGuestRole() {
        try {
            List<Role> allRoles = roleRepository.findAll();
            return allRoles.stream()
                    .filter(role -> "ROLE_GUEST".equals(role.getUniqueName()))
                    .findFirst()
                    .orElse(null);
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
        BeanUtils.copyProperties(model, existing, "id", "password");
        userCommandHandler.validate(existing);
        return userRepository.update(existing);
    }

    @Override
    @Transactional
    @CacheEvict(value = { "user_all", "user" }, allEntries = true)
    public void delete(Long id) {
        this.getById(id);
        log.debug("::> Deleting user with id {}", id);
        userRepository.delete(id);
    }

    @Override
    @Transactional
    @CacheEvict(value = { "user_all", "user" }, allEntries = true)
    public void deleteAll(List<Long> ids) {
        log.debug("::> Bulk deleting users with ids {}", ids);
        userRepository.deleteAll(ids);
    }

    @Override
    @Transactional
    @CacheEvict(value = { "user_all", "user" }, allEntries = true)
    public User toggleEnabled(Long id) {
        log.debug("::> Toggling enabled for user with id {}", id);
        User existing = this.getById(id);
        existing.setEnabled(!Boolean.TRUE.equals(existing.getEnabled()));
        return userRepository.update(existing);
    }

    @Override
    @Transactional
    @CacheEvict(value = { "user_all", "user" }, allEntries = true)
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
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        requestPasswordReset(email, "es");
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email, String lang) {
        log.debug("::> Password reset requested for email: {}", email);
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            log.warn("::> Password reset requested for non-existent email: {}", email);
            return;
        }
        if (!Boolean.TRUE.equals(user.getEnabled())) {
            log.warn("::> Password reset requested for disabled account: {}", email);
            return;
        }

        String resetToken = UUID.randomUUID().toString().replace("-", "");
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiry(Instant.now().plus(30, ChronoUnit.MINUTES));
        userRepository.update(user);

        sendPasswordResetEmail(user, resetToken, lang != null ? lang : "es");
        log.debug("::> Password reset token generated for user {}", email);
    }

    private void sendPasswordResetEmail(User user, String resetToken, String lang) {
        notificationProducerService.ifPresent(producer -> {
            String resetUrl = activationBaseUrl + "/reset-password.html?token=" + resetToken;

            Map<String, String> variables = new HashMap<>();
            variables.put("name", user.getName());
            variables.put("resetUrl", resetUrl);
            variables.put("lang", lang);

            String subject = "en".equals(lang)
                    ? "Reset your password on NEXA"
                    : "Recupera tu contraseña en NEXA";

            EmailNotificationEvent event = EmailNotificationEvent.newBuilder()
                    .setRecipient(user.getEmail())
                    .setSubject(subject)
                    .setTemplateName("password-reset")
                    .setVariables(variables)
                    .build();

            producer.sendNotificationEvent(event);
            log.debug("::> Password reset email event sent for user {}", user.getEmail());
        });
    }

    @Override
    @Transactional
    @CacheEvict(value = { "user_all", "user" }, allEntries = true)
    public void resetPassword(String token, String newPassword) {
        log.debug("::> Resetting password with token");
        User user = userRepository.findByPasswordResetToken(token);
        if (user == null) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(),
                    "El enlace de recuperación es inválido o ya fue utilizado.");
        }
        if (user.getPasswordResetTokenExpiry() != null
                && Instant.now().isAfter(user.getPasswordResetTokenExpiry())) {
            throw new ArgumentException(VALIDATION_ERROR.getCode(),
                    "El enlace de recuperación ha expirado. Solicita uno nuevo.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.update(user);
        log.debug("::> Password reset successfully for user {}", user.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "user", key = "#username")
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        log.debug("::> Loading user by username: {}", username);
        User user = userRepository.findUserByEmail(username);
        if (Objects.isNull(user)) {
            log.warn("::> User not found: {}", username);
            throw ENTITY_NOT_FOUND.toEntityNotFound("User", username);
        }
        log.debug("::> User loaded successfully: {} with {} roles", username,
                user.getRoles() != null ? user.getRoles().size() : 0);
        return user;
    }
}
