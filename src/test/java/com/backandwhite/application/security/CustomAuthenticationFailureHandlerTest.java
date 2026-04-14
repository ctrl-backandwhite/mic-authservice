package com.backandwhite.application.security;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;

class CustomAuthenticationFailureHandlerTest {

    @Test
    void onAuthenticationFailure_disabledException_redirectsToDisabledUrl() throws Exception {
        CustomAuthenticationFailureHandler handler = new CustomAuthenticationFailureHandler();
        RedirectStrategy redirectStrategy = mock(RedirectStrategy.class);
        handler.setRedirectStrategy(redirectStrategy);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        DisabledException exception = new DisabledException("Account is disabled");

        handler.onAuthenticationFailure(request, response, exception);

        verify(redirectStrategy).sendRedirect(eq(request), eq(response), eq("/login?error=disabled"));
    }

    @Test
    void onAuthenticationFailure_otherException_redirectsToGenericError() throws Exception {
        CustomAuthenticationFailureHandler handler = new CustomAuthenticationFailureHandler();
        RedirectStrategy redirectStrategy = mock(RedirectStrategy.class);
        handler.setRedirectStrategy(redirectStrategy);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException exception = new AuthenticationException("Bad credentials") {};

        handler.onAuthenticationFailure(request, response, exception);

        verify(redirectStrategy).sendRedirect(eq(request), eq(response), eq("/login?error"));
    }
}
