package com.backandwhite.application.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.backandwhite.application.port.out.EmailNotificationRequest;
import com.backandwhite.application.port.out.NotificationEventPort;
import com.backandwhite.domain.model.User;
import com.backandwhite.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthNotificationHelperTest {

    @Mock
    private NotificationEventPort notificationEventPort;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthNotificationHelper helper;

    @Test
    void sendAuthNotification_withValidUser_sendsNotification() {
        User user = User.builder().name("Ana").email("ana@test.com").build();
        when(userRepository.findUserByEmail("ana@test.com")).thenReturn(user);

        helper.sendAuthNotification("ana@test.com", "Login Alert", "login-alert");

        verify(notificationEventPort).sendNotificationEvent(any(EmailNotificationRequest.class));
    }

    @Test
    void sendAuthNotification_withNullUsername_doesNotSend() {
        helper.sendAuthNotification(null, "Login Alert", "login-alert");

        verify(userRepository, never()).findUserByEmail(any());
        verify(notificationEventPort, never()).sendNotificationEvent(any());
    }

    @Test
    void sendAuthNotification_withBlankUsername_doesNotSend() {
        helper.sendAuthNotification("   ", "Login Alert", "login-alert");

        verify(userRepository, never()).findUserByEmail(any());
        verify(notificationEventPort, never()).sendNotificationEvent(any());
    }

    @Test
    void sendAuthNotification_whenUserNotFound_doesNotSend() {
        when(userRepository.findUserByEmail("unknown@test.com")).thenReturn(null);

        helper.sendAuthNotification("unknown@test.com", "Login Alert", "login-alert");

        verify(notificationEventPort, never()).sendNotificationEvent(any());
    }

    @Test
    void sendAuthNotification_whenUserHasNullEmail_doesNotSend() {
        User user = User.builder().name("Ana").email(null).build();
        when(userRepository.findUserByEmail("ana@test.com")).thenReturn(user);

        helper.sendAuthNotification("ana@test.com", "Login Alert", "login-alert");

        verify(notificationEventPort, never()).sendNotificationEvent(any());
    }

    @Test
    void sendAuthNotification_whenExceptionThrown_handlesGracefully() {
        when(userRepository.findUserByEmail("ana@test.com")).thenThrow(new RuntimeException("DB error"));

        helper.sendAuthNotification("ana@test.com", "Login Alert", "login-alert");

        verify(notificationEventPort, never()).sendNotificationEvent(any());
    }

    @Test
    void sendAuthNotification_trimsAndLowercasesUsername() {
        User user = User.builder().name("Ana").email("ana@test.com").build();
        when(userRepository.findUserByEmail("ana@test.com")).thenReturn(user);

        helper.sendAuthNotification("  ANA@TEST.COM  ", "Login Alert", "login-alert");

        verify(userRepository).findUserByEmail("ana@test.com");
        verify(notificationEventPort).sendNotificationEvent(any(EmailNotificationRequest.class));
    }
}
