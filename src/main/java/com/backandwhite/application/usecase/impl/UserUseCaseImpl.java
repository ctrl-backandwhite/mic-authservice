package com.backandwhite.application.usecase.impl;

import com.backandwhite.application.usecase.UserUseCase;
import com.backandwhite.domain.model.User;
import com.backandwhite.domain.repository.UserRepository;

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
import java.util.List;
import java.util.Objects;
import static com.backandwhite.common.exception.Message.ENTITY_NOT_FOUND;

@Log4j2
@Service
@AllArgsConstructor
public class UserUseCaseImpl implements UserUseCase, UserDetailsService  {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserCommandHandler userCommandHandler;


    @Override
    @Transactional
    @CacheEvict(value = "user_all", allEntries = true)
    public User save(User model) {
        model.setPassword(passwordEncoder.encode(model.getPassword()));
        log.debug("::> Creating user {}", model);
        userCommandHandler.validate(model);
        return userRepository.save(model);
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
        BeanUtils.copyProperties(model, existing, "id");
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
    @Cacheable(value = "user", key = "#username")
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(username);
        if (Objects.isNull(user)) {
            throw ENTITY_NOT_FOUND.toEntityNotFound("User", username);
        }
        return userRepository.findUserByEmail(username);
    }
}
