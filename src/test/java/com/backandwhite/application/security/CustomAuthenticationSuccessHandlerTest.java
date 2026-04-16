package com.backandwhite.application.security;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backandwhite.application.port.out.EmailNotificationRequest;
import com.backandwhite.application.port.out.NotificationEventPort;
import com.backandwhite.domain.model.User;
import com.backandwhite.domain.repository.UserRepository;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationSuccessHandlerTest {

    @Mock
    private NotificationEventPort notificationEventPort;

    @Mock
    private UserRepository userRepository;

    private CustomAuthenticationSuccessHandler buildHandler() {
        CustomAuthenticationSuccessHandler handler = new CustomAuthenticationSuccessHandler(notificationEventPort,
                userRepository);
        RedirectStrategy noOpRedirect = mock(RedirectStrategy.class);
        handler.setRedirectStrategy(noOpRedirect);
        return handler;
    }

    @Test
    void onAuthenticationSuccess_whenUserFound_sendsNotification() throws Exception {
        CustomAuthenticationSuccessHandler handler = buildHandler();
        User user = User.builder().email("user@test.com").name("Ana").build();
        when(userRepository.findUserByEmail("user@test.com")).thenReturn(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@test.com");

        handler.onAuthenticationSuccess(request, response, authentication);

        await().atMost(Duration.ofSeconds(1)).untilAsserted(
                () -> verify(notificationEventPort).sendNotificationEvent(any(EmailNotificationRequest.class)));
    }

    @Test
    void onAuthenticationSuccess_whenUserNotFound_doesNotSendNotification() throws Exception {
        CustomAuthenticationSuccessHandler handler = buildHandler();
        when(userRepository.findUserByEmail("unknown@test.com")).thenReturn(null);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("unknown@test.com");

        handler.onAuthenticationSuccess(request, response, authentication);

        await().atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(userRepository).findUserByEmail("unknown@test.com"));
        verify(notificationEventPort, never()).sendNotificationEvent(any());
    }

    @Test
    void onAuthenticationSuccess_whenUserEmailIsNull_doesNotSendNotification() throws Exception {
        CustomAuthenticationSuccessHandler handler = buildHandler();
        User user = User.builder().email(null).name("Ana").build();
        when(userRepository.findUserByEmail("user@test.com")).thenReturn(user);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@test.com");

        handler.onAuthenticationSuccess(request, response, authentication);

        await().atMost(Duration.ofSeconds(1))
                .untilAsserted(() -> verify(userRepository).findUserByEmail("user@test.com"));
        verify(notificationEventPort, never()).sendNotificationEvent(any());
    }

    @Test
    void onAuthenticationSuccess_whenNotificationThrows_doesNotPropagateException() throws Exception {
        CustomAuthenticationSuccessHandler handler = buildHandler();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@test.com");

        // Should not throw — notification runs async
        handler.onAuthenticationSuccess(request, response, authentication);
    }
}
