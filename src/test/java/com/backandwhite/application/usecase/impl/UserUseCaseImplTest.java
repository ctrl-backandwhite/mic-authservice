package com.backandwhite.application.usecase.impl;

import com.backandwhite.application.handler.UserCommandHandler;
import com.backandwhite.application.service.NotificationProducerService;
import com.backandwhite.common.exception.EntityNotFoundException;
import com.backandwhite.domain.model.User;
import com.backandwhite.domain.model.Role;
import com.backandwhite.domain.repository.UserRepository;
import com.backandwhite.domain.repository.RoleRepository;
import com.backandwhite.provider.UserProvider;
import com.backandwhite.provider.RoleProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static com.backandwhite.provider.UserProvider.otherUser;
import static com.backandwhite.provider.UserProvider.user;
import static com.backandwhite.provider.RoleProvider.guestRole;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserCommandHandler userCommandHandler;

    @Spy
    private Optional<NotificationProducerService> notificationProducerService = Optional.empty();

    @InjectMocks
    private UserUseCaseImpl userUseCase;

    @Test
    void save_validUser_encodesPasswordAndDelegates() {
        User input = user().withId(null);
        User saved = user().withPassword("encoded-secret");

        when(passwordEncoder.encode(UserProvider.USER_PASSWORD)).thenReturn("encoded-secret");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userUseCase.save(input);

        assertSame(saved, result);
        assertThat(input.getPassword()).isEqualTo("encoded-secret");
        verify(passwordEncoder).encode(UserProvider.USER_PASSWORD);
        verify(userCommandHandler).validate(input);
        verify(userRepository).save(input);
    }

    @Test
    void save_userWithoutRoles_assignsDefaultGuestRole() {
        User input = user().withId(null).withRoles(List.of());
        User saved = user().withPassword("encoded-secret").withRoles(List.of(guestRole()));

        when(roleRepository.findAll()).thenReturn(List.of(guestRole()));
        when(passwordEncoder.encode(UserProvider.USER_PASSWORD)).thenReturn("encoded-secret");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userUseCase.save(input);

        assertThat(result.getRoles()).isNotEmpty();
        assertThat(result.getRoles()).containsExactly(guestRole());
        verify(passwordEncoder).encode(UserProvider.USER_PASSWORD);
        verify(userCommandHandler).validate(any(User.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void findAll_returnsRepositoryList() {
        List<User> users = List.of(user(), otherUser());

        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userUseCase.findAll();

        assertSame(users, result);
        verify(userRepository).findAll();
    }

    @Test
    void getById_existingUser_returnsUser() {
        User model = user().withId(5L);

        when(userRepository.getById(5L)).thenReturn(model);

        User result = userUseCase.getById(5L);

        assertSame(model, result);
        verify(userRepository).getById(5L);
    }

    @Test
    void getById_missingUser_throwsEntityNotFound() {
        when(userRepository.getById(10L)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> userUseCase.getById(10L));
        verify(userRepository).getById(10L);
    }

    @Test
    void update_existingUser_copiesFieldsAndPersists() {
        User existing = user().withId(10L).withPassword("old-secret");
        User update = otherUser().withId(99L);

        when(userRepository.getById(10L)).thenReturn(existing);
        when(userRepository.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userUseCase.update(update, 10L);

        verify(userCommandHandler).validate(update);
        verify(userRepository).update(any(User.class));
        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(otherUser().withId(10L).withPassword("old-secret"));
    }

    @Test
    void delete_existingUser_delegatesToRepository() {
        User existing = otherUser().withId(7L);

        when(userRepository.getById(7L)).thenReturn(existing);

        userUseCase.delete(7L);

        verify(userRepository).delete(7L);
    }

    @Test
    void loadUserByUsername_existingUser_returnsUserDetails() {
        User model = user();

        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(model);

        assertSame(model, userUseCase.loadUserByUsername(UserProvider.USER_EMAIL));
        verify(userRepository).findUserByEmail(UserProvider.USER_EMAIL);
    }

    @Test
    void loadUserByUsername_missingUser_throwsEntityNotFound() {
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> userUseCase.loadUserByUsername(UserProvider.USER_EMAIL));
        verify(userRepository).findUserByEmail(UserProvider.USER_EMAIL);
    }
}
