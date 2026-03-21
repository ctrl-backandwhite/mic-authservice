package com.backandwhite.application.usecase.impl;

import com.backandwhite.application.usecase.UserUseCase;
import com.backandwhite.common.exception.ArgumentException;
import com.backandwhite.domain.model.User;
import com.backandwhite.domain.model.Role;
import com.backandwhite.domain.repository.UserRepository;
import com.backandwhite.domain.repository.RoleRepository;

import com.backandwhite.application.handler.UserCommandHandler;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import static com.backandwhite.common.exception.Message.ENTITY_NOT_FOUND;
import static com.backandwhite.common.exception.Message.VALIDATION_ERROR;

@Log4j2
@Service
@AllArgsConstructor
public class UserUseCaseImpl implements UserUseCase, UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserCommandHandler userCommandHandler;

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
        log.debug("::> Creating user {}", model);
        userCommandHandler.validate(model);
        return userRepository.save(model);
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
