package com.backandwhite.api.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private OAuth2AuthorizationService authorizationService;

    @InjectMocks
    private AuthController controller;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void revokeToken_whenAuthorizationFound_returnsNoContent() {
        OAuth2Authorization authorization = mock(OAuth2Authorization.class);
        when(authorization.getRegisteredClientId()).thenReturn("client-id");
        when(authorizationService.findByToken("token", OAuth2TokenType.ACCESS_TOKEN)).thenReturn(authorization);

        ResponseEntity<Void> response = controller.revokeToken("token", "access_token", "test-nx-token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(authorizationService).remove(authorization);
    }

    @Test
    void revokeToken_whenNotFound_returnsNotFound() {
        when(authorizationService.findByToken("token", OAuth2TokenType.ACCESS_TOKEN)).thenReturn(null);

        ResponseEntity<Void> response = controller.revokeToken("token", "access_token", "test-nx-token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(authorizationService, never()).remove(any());
    }

    @Test
    void revokeToken_whenException_returnsServerError() {
        when(authorizationService.findByToken("token", OAuth2TokenType.ACCESS_TOKEN))
                .thenThrow(new RuntimeException("boom"));

        ResponseEntity<Void> response = controller.revokeToken("token", "access_token", "test-nx-token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void logout_withoutAuthentication_returnsNoContent() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getScheme()).thenReturn("http");
        when(request.isSecure()).thenReturn(false);
        when(request.getSession(false)).thenReturn(null);
        when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("foo", "bar") });

        ResponseEntity<Void> result = controller.logout(request, response, null, "test-nx-token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(response, atLeast(3)).addCookie(any(Cookie.class));
        verify(authorizationService, never()).remove(any());
    }
}
