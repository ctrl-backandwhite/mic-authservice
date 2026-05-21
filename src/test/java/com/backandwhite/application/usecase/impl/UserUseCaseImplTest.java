package com.backandwhite.application.usecase.impl;

import static com.backandwhite.provider.RoleProvider.guestRole;
import static com.backandwhite.provider.UserProvider.otherUser;
import static com.backandwhite.provider.UserProvider.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backandwhite.application.handler.UserCommandHandler;
import com.backandwhite.application.mapper.UserUpdateMapper;
import com.backandwhite.application.port.out.AuthEventPort;
import com.backandwhite.application.port.out.NotificationEventPort;
import com.backandwhite.common.exception.ArgumentException;
import com.backandwhite.common.exception.EntityNotFoundException;
import com.backandwhite.domain.model.User;
import com.backandwhite.domain.model.UserSession;
import com.backandwhite.domain.repository.RoleRepository;
import com.backandwhite.domain.repository.UserRepository;
import com.backandwhite.domain.repository.UserSessionRepository;
import com.backandwhite.provider.UserProvider;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;

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

    @Mock
    private NotificationEventPort notificationEventPort;

    @Mock
    private AuthEventPort authEventPort;

    @Mock
    private UserSessionRepository userSessionRepository;

    @Mock
    private OAuth2AuthorizationService authorizationService;

    @Mock
    private UserUpdateMapper userUpdateMapper;

    @InjectMocks
    private UserUseCaseImpl userUseCase;

    // ─── save ─────────────────────────────────────────────

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
    void save_emailAlreadyExists_throwsArgumentException() {
        User input = user().withId(null);
        when(userRepository.findUserByEmail(input.getEmail())).thenReturn(user());

        assertThrows(ArgumentException.class, () -> userUseCase.save(input));
        verify(userRepository, never()).save(any());
    }

    @Test
    void save_userWithNullRoles_assignsGuestRole() {
        User input = user().withId(null).withRoles(null);
        User saved = user().withPassword("enc");

        when(roleRepository.findAll()).thenReturn(List.of(guestRole()));
        when(passwordEncoder.encode(any())).thenReturn("enc");
        when(userRepository.save(any())).thenReturn(saved);

        userUseCase.save(input);

        assertThat(input.getRoles()).isNotEmpty();
    }

    @Test
    void save_guestRoleNotFound_proceedsWithoutRole() {
        User input = user().withId(null).withRoles(List.of());
        User saved = user().withPassword("enc").withRoles(List.of());

        when(roleRepository.findAll()).thenReturn(List.of());
        when(passwordEncoder.encode(any())).thenReturn("enc");
        when(userRepository.save(any())).thenReturn(saved);

        userUseCase.save(input);

        verify(userRepository).save(input);
    }

    @Test
    void save_roleRepositoryThrows_proceedsWithoutRole() {
        User input = user().withId(null).withRoles(List.of());
        User saved = user().withPassword("enc");

        when(roleRepository.findAll()).thenThrow(new RuntimeException("DB error"));
        when(passwordEncoder.encode(any())).thenReturn("enc");
        when(userRepository.save(any())).thenReturn(saved);

        userUseCase.save(input);

        verify(userRepository).save(input);
    }

    // ─── findAll / getById ────────────────────────────────

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

    // ─── update ────────────────────────────────────────────

    @Test
    void update_existingUser_copiesFieldsAndPersists() {
        User existing = user().withId(10L).withPassword("old-secret");
        User update = otherUser().withId(99L);

        when(userRepository.getById(10L)).thenReturn(existing);
        doAnswer(inv -> {
            BeanUtils.copyProperties(inv.getArgument(0), inv.getArgument(1), "id", "password", "authorities");
            return null;
        }).when(userUpdateMapper).updateFromModel(any(User.class), any(User.class));
        when(userRepository.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userUseCase.update(update, 10L);

        verify(userCommandHandler).validate(argThat(u -> u.getId().equals(10L)));
        verify(userRepository).update(any(User.class));
        assertThat(result).usingRecursiveComparison().isEqualTo(otherUser().withId(10L).withPassword("old-secret"));
    }

    // ─── delete / deleteAll ────────────────────────────────

    @Test
    void delete_existingUser_delegatesToRepository() {
        User existing = otherUser().withId(7L);

        when(userRepository.getById(7L)).thenReturn(existing);

        userUseCase.delete(7L);

        verify(userRepository).delete(7L);
    }

    @Test
    void deleteAll_delegatesToRepository() {
        List<Long> ids = List.of(1L, 2L, 3L);

        userUseCase.deleteAll(ids);

        verify(userRepository).deleteAll(ids);
    }

    // ─── toggleEnabled ────────────────────────────────────

    @Test
    void toggleEnabled_enabledUser_disablesUser() {
        User existing = user().withId(5L).withEnabled(true);
        when(userRepository.getById(5L)).thenReturn(existing);
        when(userRepository.update(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = userUseCase.toggleEnabled(5L);

        assertThat(result.getEnabled()).isFalse();
    }

    @Test
    void toggleEnabled_disabledUser_enablesUser() {
        User existing = user().withId(5L).withEnabled(false);
        when(userRepository.getById(5L)).thenReturn(existing);
        when(userRepository.update(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = userUseCase.toggleEnabled(5L);

        assertThat(result.getEnabled()).isTrue();
    }

    // ─── activateUser ──────────────────────────────────────

    @Test
    void activateUser_validToken_activatesUser() {
        User foundUser = user().withEnabled(false).withActivationToken("valid-token")
                .withActivationTokenExpiry(Instant.now().plus(1, ChronoUnit.HOURS));
        when(userRepository.findByActivationToken("valid-token")).thenReturn(foundUser);

        userUseCase.activateUser("valid-token", "es");

        assertThat(foundUser.getEnabled()).isTrue();
        assertThat(foundUser.getActivationToken()).isNull();
        verify(userRepository).update(foundUser);
    }

    @Test
    void activateUser_invalidToken_throwsException() {
        when(userRepository.findByActivationToken("bad-token")).thenReturn(null);

        assertThrows(ArgumentException.class, () -> userUseCase.activateUser("bad-token", "es"));
    }

    @Test
    void activateUser_expiredToken_throwsException() {
        User foundUser = user().withEnabled(false).withActivationToken("expired")
                .withActivationTokenExpiry(Instant.now().minus(1, ChronoUnit.HOURS));
        when(userRepository.findByActivationToken("expired")).thenReturn(foundUser);

        assertThrows(ArgumentException.class, () -> userUseCase.activateUser("expired", "es"));
    }

    @Test
    void activateUser_alreadyActivated_throwsException() {
        User foundUser = user().withEnabled(true).withActivationToken("tok")
                .withActivationTokenExpiry(Instant.now().plus(1, ChronoUnit.HOURS));
        when(userRepository.findByActivationToken("tok")).thenReturn(foundUser);

        assertThrows(ArgumentException.class, () -> userUseCase.activateUser("tok", "es"));
    }

    // ─── requestPasswordReset ──────────────────────────────

    @Test
    void requestPasswordReset_existingEnabledUser_sendsEmail() {
        User foundUser = user().withEnabled(true);
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);

        userUseCase.requestPasswordReset(UserProvider.USER_EMAIL, "es");

        verify(userRepository).update(foundUser);
        verify(notificationEventPort).sendNotificationEvent(any());
    }

    @Test
    void requestPasswordReset_nonExistentEmail_returnsSilently() {
        when(userRepository.findUserByEmail("unknown@test.com")).thenReturn(null);

        userUseCase.requestPasswordReset("unknown@test.com", "es");

        verify(userRepository, never()).update(any());
    }

    @Test
    void requestPasswordReset_disabledAccount_returnsSilently() {
        User foundUser = user().withEnabled(false);
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);

        userUseCase.requestPasswordReset(UserProvider.USER_EMAIL, "es");

        verify(userRepository, never()).update(any());
    }

    @Test
    void requestPasswordReset_nullEmail_returnsSilently() {
        when(userRepository.findUserByEmail(null)).thenReturn(null);

        userUseCase.requestPasswordReset(null, "es");

        verify(userRepository, never()).update(any());
    }

    // ─── resetPassword ─────────────────────────────────────

    @Test
    void resetPassword_validToken_resetsPassword() {
        User foundUser = user().withPasswordResetToken("reset-tok")
                .withPasswordResetTokenExpiry(Instant.now().plus(1, ChronoUnit.HOURS));
        when(userRepository.findByPasswordResetToken("reset-tok")).thenReturn(foundUser);
        when(passwordEncoder.encode("NewPass1")).thenReturn("encoded-new");

        userUseCase.resetPassword("reset-tok", "NewPass1");

        assertThat(foundUser.getPassword()).isEqualTo("encoded-new");
        assertThat(foundUser.getPasswordResetToken()).isNull();
        verify(userRepository).update(foundUser);
    }

    @Test
    void resetPassword_invalidToken_throwsException() {
        when(userRepository.findByPasswordResetToken("bad")).thenReturn(null);

        assertThrows(ArgumentException.class, () -> userUseCase.resetPassword("bad", "NewPass1"));
    }

    @Test
    void resetPassword_expiredToken_throwsException() {
        User foundUser = user().withPasswordResetToken("exp-tok")
                .withPasswordResetTokenExpiry(Instant.now().minus(1, ChronoUnit.HOURS));
        when(userRepository.findByPasswordResetToken("exp-tok")).thenReturn(foundUser);

        assertThrows(ArgumentException.class, () -> userUseCase.resetPassword("exp-tok", "NewPass1"));
    }

    // ─── requestPasswordChange ─────────────────────────────

    @Test
    void requestPasswordChange_valid_generatesCode() {
        User foundUser = user();
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);
        when(passwordEncoder.matches("secret", foundUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("NewPass1")).thenReturn("$2a$encoded");
        when(userRepository.findByPasswordChangeCode(anyString())).thenReturn(null);

        userUseCase.requestPasswordChange(UserProvider.USER_EMAIL, "secret", "NewPass1", "NewPass1", "es");

        assertThat(foundUser.getPasswordChangeCode()).isNotNull();
        verify(userRepository).update(foundUser);
        verify(notificationEventPort).sendNotificationEvent(any());
    }

    @Test
    void requestPasswordChange_userNotFound_throwsException() {
        when(userRepository.findUserByEmail("none@test.com")).thenReturn(null);

        assertThrows(ArgumentException.class,
                () -> userUseCase.requestPasswordChange("none@test.com", "old", "new", "new", "es"));
    }

    @Test
    void requestPasswordChange_wrongCurrentPassword_throwsException() {
        User foundUser = user();
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);
        when(passwordEncoder.matches("wrong", foundUser.getPassword())).thenReturn(false);

        assertThrows(ArgumentException.class,
                () -> userUseCase.requestPasswordChange(UserProvider.USER_EMAIL, "wrong", "new", "new", "es"));
    }

    @Test
    void requestPasswordChange_passwordsMismatch_throwsException() {
        User foundUser = user();
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);
        when(passwordEncoder.matches("secret", foundUser.getPassword())).thenReturn(true);

        assertThrows(ArgumentException.class,
                () -> userUseCase.requestPasswordChange(UserProvider.USER_EMAIL, "secret", "new1", "new2", "es"));
    }

    // ─── confirmPasswordChange ─────────────────────────────

    @Test
    void confirmPasswordChange_validCode_changesPassword() {
        User foundUser = user().withPasswordChangeCode("123456")
                .withPasswordChangeCodeExpiry(Instant.now().plus(1, ChronoUnit.MINUTES))
                .withPasswordResetToken("$2a$encodedNew");
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);

        userUseCase.confirmPasswordChange(UserProvider.USER_EMAIL, "123456");

        assertThat(foundUser.getPassword()).isEqualTo("$2a$encodedNew");
        assertThat(foundUser.getPasswordChangeCode()).isNull();
        assertThat(foundUser.getPasswordResetToken()).isNull();
        verify(userRepository).update(foundUser);
    }

    @Test
    void confirmPasswordChange_userNotFound_throwsException() {
        when(userRepository.findUserByEmail("none@test.com")).thenReturn(null);

        assertThrows(ArgumentException.class, () -> userUseCase.confirmPasswordChange("none@test.com", "123456"));
    }

    @Test
    void confirmPasswordChange_invalidCode_throwsException() {
        User foundUser = user().withPasswordChangeCode("123456");
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);

        assertThrows(ArgumentException.class,
                () -> userUseCase.confirmPasswordChange(UserProvider.USER_EMAIL, "000000"));
    }

    @Test
    void confirmPasswordChange_expiredCode_throwsException() {
        User foundUser = user().withPasswordChangeCode("123456")
                .withPasswordChangeCodeExpiry(Instant.now().minus(1, ChronoUnit.MINUTES));
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);

        assertThrows(ArgumentException.class,
                () -> userUseCase.confirmPasswordChange(UserProvider.USER_EMAIL, "123456"));
        verify(userRepository).update(foundUser);
    }

    @Test
    void confirmPasswordChange_noStoredPasswordToken_keepsExistingPassword() {
        User foundUser = user().withPasswordChangeCode("123456")
                .withPasswordChangeCodeExpiry(Instant.now().plus(1, ChronoUnit.MINUTES)).withPasswordResetToken(null);
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);

        userUseCase.confirmPasswordChange(UserProvider.USER_EMAIL, "123456");

        assertThat(foundUser.getPassword()).isEqualTo(UserProvider.USER_PASSWORD);
    }

    // ─── loadUserByUsername ────────────────────────────────

    @Test
    void loadUserByUsername_existingUser_returnsUserDetails() {
        User model = user();

        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(model);

        assertSame(model, userUseCase.loadUserByUsername(UserProvider.USER_EMAIL));
        verify(userRepository).findUserByEmail(UserProvider.USER_EMAIL);
    }

    @Test
    void loadUserByUsername_fallsBackToNickName() {
        User model = user();
        when(userRepository.findUserByEmail(UserProvider.USER_NICK_NAME)).thenReturn(null);
        when(userRepository.findUserByNickName(UserProvider.USER_NICK_NAME)).thenReturn(model);

        assertSame(model, userUseCase.loadUserByUsername(UserProvider.USER_NICK_NAME));
    }

    @Test
    void loadUserByUsername_missingUser_throwsEntityNotFound() {
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> userUseCase.loadUserByUsername(UserProvider.USER_EMAIL));
        verify(userRepository).findUserByEmail(UserProvider.USER_EMAIL);
    }

    @Test
    void loadUserByUsername_normalizesInput() {
        User model = user();
        when(userRepository.findUserByEmail("ana.lopez@example.com")).thenReturn(model);

        assertSame(model, userUseCase.loadUserByUsername("  Ana.Lopez@Example.com  "));
    }

    // ─── getActiveSessions ────────────────────────────────

    @Test
    void getActiveSessions_existingUser_returnsSessions() {
        User foundUser = user();
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);
        List<UserSession> sessions = List.of(UserSession.builder().sessionId("s1").userId(1L).build());
        when(userSessionRepository.findActiveByUserId(1L)).thenReturn(sessions);

        List<UserSession> result = userUseCase.getActiveSessions(UserProvider.USER_EMAIL);

        assertThat(result).hasSize(1);
    }

    @Test
    void getActiveSessions_userNotFound_returnsEmptyList() {
        when(userRepository.findUserByEmail("none@test.com")).thenReturn(null);

        List<UserSession> result = userUseCase.getActiveSessions("none@test.com");

        assertThat(result).isEmpty();
    }

    // ─── requestSessionRevoke ─────────────────────────────

    @Test
    void requestSessionRevoke_valid_sendsCode() {
        User foundUser = user();
        UserSession session = UserSession.builder().sessionId("sess-1").userId(1L).revoked(false).build();
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);
        when(userSessionRepository.findBySessionId("sess-1")).thenReturn(session);

        userUseCase.requestSessionRevoke(UserProvider.USER_EMAIL, "sess-1", "es");

        assertThat(foundUser.getSessionRevokeCode()).isNotNull();
        verify(userRepository).update(foundUser);
        verify(notificationEventPort).sendNotificationEvent(any());
    }

    @Test
    void requestSessionRevoke_userNotFound_throwsException() {
        when(userRepository.findUserByEmail("none@test.com")).thenReturn(null);

        assertThrows(ArgumentException.class, () -> userUseCase.requestSessionRevoke("none@test.com", "sess-1", "es"));
    }

    @Test
    void requestSessionRevoke_sessionNotFound_throwsException() {
        User foundUser = user();
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);
        when(userSessionRepository.findBySessionId("sess-x")).thenReturn(null);

        assertThrows(ArgumentException.class,
                () -> userUseCase.requestSessionRevoke(UserProvider.USER_EMAIL, "sess-x", "es"));
    }

    @Test
    void requestSessionRevoke_sessionAlreadyRevoked_throwsException() {
        User foundUser = user();
        UserSession session = UserSession.builder().sessionId("sess-1").userId(1L).revoked(true).build();
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);
        when(userSessionRepository.findBySessionId("sess-1")).thenReturn(session);

        assertThrows(ArgumentException.class,
                () -> userUseCase.requestSessionRevoke(UserProvider.USER_EMAIL, "sess-1", "es"));
    }

    @Test
    void requestSessionRevoke_sessionBelongsToDifferentUser_throwsException() {
        User foundUser = user().withId(1L);
        UserSession session = UserSession.builder().sessionId("sess-1").userId(99L).revoked(false).build();
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);
        when(userSessionRepository.findBySessionId("sess-1")).thenReturn(session);

        assertThrows(ArgumentException.class,
                () -> userUseCase.requestSessionRevoke(UserProvider.USER_EMAIL, "sess-1", "es"));
    }

    // ─── confirmSessionRevoke ─────────────────────────────

    @Test
    void confirmSessionRevoke_validCode_revokesSession() {
        User foundUser = user().withSessionRevokeCode("654321")
                .withSessionRevokeCodeExpiry(Instant.now().plus(1, ChronoUnit.MINUTES)).withSessionToRevoke("sess-1");
        UserSession session = UserSession.builder().sessionId("sess-1").authorizationId("auth-id").build();
        OAuth2Authorization oAuth2Auth = org.mockito.Mockito.mock(OAuth2Authorization.class);

        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);
        when(userSessionRepository.findBySessionId("sess-1")).thenReturn(session);
        when(authorizationService.findById("auth-id")).thenReturn(oAuth2Auth);

        userUseCase.confirmSessionRevoke(UserProvider.USER_EMAIL, "654321");

        verify(userSessionRepository).revokeSession("sess-1");
        verify(authorizationService).remove(oAuth2Auth);
        assertThat(foundUser.getSessionRevokeCode()).isNull();
    }

    @Test
    void confirmSessionRevoke_userNotFound_throwsException() {
        when(userRepository.findUserByEmail("none@test.com")).thenReturn(null);

        assertThrows(ArgumentException.class, () -> userUseCase.confirmSessionRevoke("none@test.com", "123456"));
    }

    @Test
    void confirmSessionRevoke_invalidCode_throwsException() {
        User foundUser = user().withSessionRevokeCode("654321");
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);

        assertThrows(ArgumentException.class,
                () -> userUseCase.confirmSessionRevoke(UserProvider.USER_EMAIL, "000000"));
    }

    @Test
    void confirmSessionRevoke_expiredCode_throwsException() {
        User foundUser = user().withSessionRevokeCode("654321")
                .withSessionRevokeCodeExpiry(Instant.now().minus(1, ChronoUnit.MINUTES));
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);

        assertThrows(ArgumentException.class,
                () -> userUseCase.confirmSessionRevoke(UserProvider.USER_EMAIL, "654321"));
    }

    @Test
    void confirmSessionRevoke_nullSessionId_doesNotRevoke() {
        User foundUser = user().withSessionRevokeCode("654321")
                .withSessionRevokeCodeExpiry(Instant.now().plus(1, ChronoUnit.MINUTES)).withSessionToRevoke(null);
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);

        userUseCase.confirmSessionRevoke(UserProvider.USER_EMAIL, "654321");

        verify(userSessionRepository, never()).revokeSession(any());
    }

    @Test
    void confirmSessionRevoke_sessionNotFoundInDb_skipsRevoke() {
        User foundUser = user().withSessionRevokeCode("654321")
                .withSessionRevokeCodeExpiry(Instant.now().plus(1, ChronoUnit.MINUTES))
                .withSessionToRevoke("sess-missing");
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);
        when(userSessionRepository.findBySessionId("sess-missing")).thenReturn(null);

        userUseCase.confirmSessionRevoke(UserProvider.USER_EMAIL, "654321");

        verify(userSessionRepository, never()).revokeSession(any());
    }

    @Test
    void confirmSessionRevoke_noAuthorizationId_skipsOAuth2Revoke() {
        User foundUser = user().withSessionRevokeCode("654321")
                .withSessionRevokeCodeExpiry(Instant.now().plus(1, ChronoUnit.MINUTES)).withSessionToRevoke("sess-1");
        UserSession session = UserSession.builder().sessionId("sess-1").authorizationId(null).build();
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);
        when(userSessionRepository.findBySessionId("sess-1")).thenReturn(session);

        userUseCase.confirmSessionRevoke(UserProvider.USER_EMAIL, "654321");

        verify(userSessionRepository).revokeSession("sess-1");
        verify(authorizationService, never()).findById(anyString());
    }

    @Test
    void confirmSessionRevoke_oAuth2ServiceThrows_logsWarning() {
        User foundUser = user().withSessionRevokeCode("654321")
                .withSessionRevokeCodeExpiry(Instant.now().plus(1, ChronoUnit.MINUTES)).withSessionToRevoke("sess-1");
        UserSession session = UserSession.builder().sessionId("sess-1").authorizationId("auth-id").build();
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);
        when(userSessionRepository.findBySessionId("sess-1")).thenReturn(session);
        when(authorizationService.findById("auth-id")).thenThrow(new RuntimeException("DB error"));

        userUseCase.confirmSessionRevoke(UserProvider.USER_EMAIL, "654321");

        verify(userSessionRepository).revokeSession("sess-1");
    }

    @Test
    void confirmSessionRevoke_oAuth2AuthorizationNotFound_skipsRemove() {
        // Exercises auth==null branch in revokeOAuth2Authorization (line 529).
        User foundUser = user().withSessionRevokeCode("654321")
                .withSessionRevokeCodeExpiry(Instant.now().plus(1, ChronoUnit.MINUTES)).withSessionToRevoke("sess-1");
        UserSession session = UserSession.builder().sessionId("sess-1").authorizationId("auth-vanished").build();
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);
        when(userSessionRepository.findBySessionId("sess-1")).thenReturn(session);
        when(authorizationService.findById("auth-vanished")).thenReturn(null);

        userUseCase.confirmSessionRevoke(UserProvider.USER_EMAIL, "654321");

        verify(userSessionRepository).revokeSession("sess-1");
        verify(authorizationService, never()).remove(any(OAuth2Authorization.class));
    }

    @Test
    void activateUser_nullExpiry_activatesUser() {
        // Exercises the (expiry == null) branch at line 222.
        User foundUser = user().withEnabled(false).withActivationToken("tok-no-exp").withActivationTokenExpiry(null);
        when(userRepository.findByActivationToken("tok-no-exp")).thenReturn(foundUser);

        userUseCase.activateUser("tok-no-exp", "es");

        assertThat(foundUser.getEnabled()).isTrue();
        verify(userRepository).update(foundUser);
    }

    @Test
    void resetPassword_nullExpiry_resetsPassword() {
        // Exercises the (passwordResetTokenExpiry == null) branch at line 299.
        User foundUser = user().withPasswordResetToken("reset-no-exp").withPasswordResetTokenExpiry(null);
        when(userRepository.findByPasswordResetToken("reset-no-exp")).thenReturn(foundUser);
        when(passwordEncoder.encode("NewPass1")).thenReturn("encoded-new");

        userUseCase.resetPassword("reset-no-exp", "NewPass1");

        assertThat(foundUser.getPassword()).isEqualTo("encoded-new");
        verify(userRepository).update(foundUser);
    }

    @Test
    void confirmPasswordChange_storedPasswordWithoutBcryptPrefix_keepsExistingPassword() {
        // Exercises applyStoredPassword branch where token is non-null but doesn't
        // start with $2a$ (line 400).
        User foundUser = user().withPasswordChangeCode("123456")
                .withPasswordChangeCodeExpiry(Instant.now().plus(1, ChronoUnit.MINUTES))
                .withPasswordResetToken("not-a-bcrypt-hash");
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);

        userUseCase.confirmPasswordChange(UserProvider.USER_EMAIL, "123456");

        assertThat(foundUser.getPassword()).isEqualTo(UserProvider.USER_PASSWORD);
        // Token preserved because it didn't pass the $2a$ check.
        assertThat(foundUser.getPasswordResetToken()).isEqualTo("not-a-bcrypt-hash");
    }

    @Test
    void confirmPasswordChange_nullCodeExpiry_treatedAsNotExpired() {
        // Exercises validateCodeExpiry branch where expiry == null (line 380).
        User foundUser = user().withPasswordChangeCode("123456").withPasswordChangeCodeExpiry(null)
                .withPasswordResetToken(null);
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);

        userUseCase.confirmPasswordChange(UserProvider.USER_EMAIL, "123456");

        verify(userRepository).update(foundUser);
        assertThat(foundUser.getPasswordChangeCode()).isNull();
    }

    @Test
    void confirmPasswordChange_nullStoredCode_throwsException() {
        // Exercises validateVerificationCode branch where storedCode == null
        // (line 374, first half of the OR).
        User foundUser = user().withPasswordChangeCode(null);
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);

        assertThrows(ArgumentException.class,
                () -> userUseCase.confirmPasswordChange(UserProvider.USER_EMAIL, "123456"));
    }

    @Test
    void requestPasswordChange_codeCollision_retriesUntilUnique() {
        // Exercises the do/while loop at line 413 by returning a non-null user
        // for the first generated code (collision) then null on retry.
        User foundUser = user();
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(foundUser);
        when(passwordEncoder.matches("secret", foundUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("NewPass1")).thenReturn("$2a$encoded");
        // First call returns a colliding user, subsequent calls return null.
        when(userRepository.findByPasswordChangeCode(anyString())).thenReturn(foundUser).thenReturn(null);

        userUseCase.requestPasswordChange(UserProvider.USER_EMAIL, "secret", "NewPass1", "NewPass1", "es");

        assertThat(foundUser.getPasswordChangeCode()).isNotNull();
        verify(userRepository, org.mockito.Mockito.atLeast(2)).findByPasswordChangeCode(anyString());
    }

    @Test
    void loadUserByUsername_userWithNullRoles_returnsUser() {
        // Exercises (roles != null) ternary at line 444 with the null branch.
        User model = user().withRoles(null);
        when(userRepository.findUserByEmail(UserProvider.USER_EMAIL)).thenReturn(model);

        assertSame(model, userUseCase.loadUserByUsername(UserProvider.USER_EMAIL));
    }
}
