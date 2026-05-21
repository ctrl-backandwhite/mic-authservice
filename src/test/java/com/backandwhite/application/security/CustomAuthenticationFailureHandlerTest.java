package com.backandwhite.application.security;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.backandwhite.application.port.out.NotificationEventPort;
import com.backandwhite.domain.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;

class CustomAuthenticationFailureHandlerTest {

    private final NotificationEventPort notificationEventPort = mock(NotificationEventPort.class);
    private final UserRepository userRepository = mock(UserRepository.class);

    @Test
    void onAuthenticationFailure_disabledException_redirectsToDisabledUrl() throws Exception {
        CustomAuthenticationFailureHandler handler = new CustomAuthenticationFailureHandler(notificationEventPort,
                userRepository);
        RedirectStrategy redirectStrategy = mock(RedirectStrategy.class);
        handler.setRedirectStrategy(redirectStrategy);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        DisabledException exception = new DisabledException("Account is disabled");

        handler.onAuthenticationFailure(request, response, exception);

        verify(redirectStrategy).sendRedirect(request, response, "/login?error=disabled");
    }

    @Test
    void onAuthenticationFailure_otherException_redirectsToGenericError() throws Exception {
        CustomAuthenticationFailureHandler handler = new CustomAuthenticationFailureHandler(notificationEventPort,
                userRepository);
        RedirectStrategy redirectStrategy = mock(RedirectStrategy.class);
        handler.setRedirectStrategy(redirectStrategy);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException exception = new AuthenticationException("Bad credentials") {
        };

        handler.onAuthenticationFailure(request, response, exception);

        verify(redirectStrategy).sendRedirect(request, response, "/login?error");
    }
}
