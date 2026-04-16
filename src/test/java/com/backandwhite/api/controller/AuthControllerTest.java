package com.backandwhite.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.backandwhite.domain.repository.UserSessionRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private OAuth2AuthorizationService authorizationService;

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private UserSessionRepository userSessionRepository;

    @InjectMocks
    private AuthController controller;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void revokeToken_whenAuthorizationFound_returnsNoContent() {
        OAuth2Authorization authorization = mock(OAuth2Authorization.class);
        when(authorizationService.findByToken("token", OAuth2TokenType.ACCESS_TOKEN)).thenReturn(authorization);

        ResponseEntity<Void> response = controller.revokeToken("token", "access_token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(authorizationService).remove(authorization);
    }

    @Test
    void revokeToken_whenNotFound_returnsNotFound() {
        when(authorizationService.findByToken("token", OAuth2TokenType.ACCESS_TOKEN)).thenReturn(null);

        ResponseEntity<Void> response = controller.revokeToken("token", "access_token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(authorizationService, never()).remove(any());
    }

    @Test
    void revokeToken_whenException_returnsServerError() {
        when(authorizationService.findByToken("token", OAuth2TokenType.ACCESS_TOKEN))
                .thenThrow(new RuntimeException("boom"));

        ResponseEntity<Void> response = controller.revokeToken("token", "access_token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void revokeToken_withRefreshTokenHint_usesRefreshTokenType() {
        OAuth2Authorization authorization = mock(OAuth2Authorization.class);
        when(authorizationService.findByToken("token", OAuth2TokenType.REFRESH_TOKEN)).thenReturn(authorization);

        ResponseEntity<Void> response = controller.revokeToken("token", "refresh_token");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(authorizationService).findByToken("token", OAuth2TokenType.REFRESH_TOKEN);
        verify(authorizationService).remove(authorization);
    }

    @Test
    void logout_withoutAuthentication_returnsNoContent() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getSession(false)).thenReturn(null);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("foo", "bar")});

        ResponseEntity<Void> result = controller.logout(request, response, null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(response, atLeast(3)).addCookie(any(Cookie.class));
        verify(authorizationService, never()).remove(any());
    }

    @Test
    void logout_withAuthentication_andToken_revokesTokenAndInvalidatesSession() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken("user", "pass", "ROLE_USER"));

        OAuth2Authorization authorization = mock(OAuth2Authorization.class);
        when(authorizationService.findByToken("my-token", OAuth2TokenType.ACCESS_TOKEN)).thenReturn(authorization);
        when(request.getSession(false)).thenReturn(session);
        when(request.getCookies()).thenReturn(null);

        ResponseEntity<Void> result = controller.logout(request, response, "my-token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(authorizationService).remove(authorization);
        verify(session, atLeastOnce()).invalidate();
    }

    @Test
    void logout_withAuthentication_noToken_doesNotRevokeToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken("user", "pass", "ROLE_USER"));

        when(request.getSession(false)).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        ResponseEntity<Void> result = controller.logout(request, response, null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(authorizationService, never()).remove(any());
    }

    @Test
    void logout_withEmptyToken_doesNotRevokeToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken("user", "pass", "ROLE_USER"));

        when(request.getSession(false)).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        ResponseEntity<Void> result = controller.logout(request, response, "");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(authorizationService, never()).remove(any());
    }

    @Test
    void logout_withBearer_revokesCurrentSession() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sid")).thenReturn("session-123");

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer my-jwt-token");
        when(jwtDecoder.decode("my-jwt-token")).thenReturn(jwt);
        when(authorizationService.findByToken("my-jwt-token", OAuth2TokenType.ACCESS_TOKEN)).thenReturn(null);
        when(request.getSession(false)).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        ResponseEntity<Void> result = controller.logout(request, response, null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userSessionRepository).revokeSession("session-123");
    }

    @Test
    void logout_withBearer_andOAuth2Authorization_removesAuthorization() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sid")).thenReturn("session-123");

        OAuth2Authorization authorization = mock(OAuth2Authorization.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer my-jwt-token");
        when(jwtDecoder.decode("my-jwt-token")).thenReturn(jwt);
        when(authorizationService.findByToken("my-jwt-token", OAuth2TokenType.ACCESS_TOKEN)).thenReturn(authorization);
        when(request.getSession(false)).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        ResponseEntity<Void> result = controller.logout(request, response, null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userSessionRepository).revokeSession("session-123");
        verify(authorizationService).remove(authorization);
    }

    @Test
    void logout_withNoBearer_skipsSessionRevocation() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        when(request.getSession(false)).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        ResponseEntity<Void> result = controller.logout(request, response, null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userSessionRepository, never()).revokeSession(any());
    }

    @Test
    void logout_withInvalidBearer_skipsSessionRevocation() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic abc");
        when(request.getSession(false)).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        ResponseEntity<Void> result = controller.logout(request, response, null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(jwtDecoder, never()).decode(any());
    }

    @Test
    void logout_whenJwtDecodeThrowsJwtException_handlesGracefully() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer invalid-token");
        when(jwtDecoder.decode("invalid-token")).thenThrow(new JwtException("bad token"));
        when(request.getSession(false)).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        ResponseEntity<Void> result = controller.logout(request, response, null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userSessionRepository, never()).revokeSession(any());
    }

    @Test
    void logout_whenJwtHasNoSidClaim_skipsSessionRevocation() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sid")).thenReturn(null);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer my-jwt-token");
        when(jwtDecoder.decode("my-jwt-token")).thenReturn(jwt);
        when(request.getSession(false)).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        ResponseEntity<Void> result = controller.logout(request, response, null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userSessionRepository, never()).revokeSession(any());
    }

    @Test
    void logout_whenJwtHasBlankSidClaim_skipsSessionRevocation() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sid")).thenReturn("   ");

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer my-jwt-token");
        when(jwtDecoder.decode("my-jwt-token")).thenReturn(jwt);
        when(request.getSession(false)).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        ResponseEntity<Void> result = controller.logout(request, response, null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userSessionRepository, never()).revokeSession(any());
    }

    @Test
    void logout_whenRevokeSessionThrowsException_handlesGracefully() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getClaimAsString("sid")).thenReturn("session-123");

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer my-jwt-token");
        when(jwtDecoder.decode("my-jwt-token")).thenReturn(jwt);
        doThrow(new RuntimeException("DB error")).when(userSessionRepository).revokeSession("session-123");
        when(request.getSession(false)).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        ResponseEntity<Void> result = controller.logout(request, response, null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void logout_withMultipleCookies_deletesAll() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        Cookie[] cookies = new Cookie[]{new Cookie("JSESSIONID", "abc"), new Cookie("custom-cookie", "value")};
        when(request.getCookies()).thenReturn(cookies);
        when(request.getSession(false)).thenReturn(null);

        ResponseEntity<Void> result = controller.logout(request, response, null);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        // 2 cookies * 3 configurations + JSESSIONID explicit * 3 = 9
        verify(response, atLeast(9)).addCookie(any(Cookie.class));
    }

    @Test
    void logout_whenPrivateRevokeTokenThrowsException_handlesGracefully() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken("user", "pass", "ROLE_USER"));

        when(authorizationService.findByToken("bad-token", OAuth2TokenType.ACCESS_TOKEN))
                .thenThrow(new RuntimeException("error"));
        when(request.getSession(false)).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        ResponseEntity<Void> result = controller.logout(request, response, "bad-token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void logout_whenPrivateRevokeTokenFindsNoAuthorization_doesNotRemove() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        SecurityContextHolder.getContext()
                .setAuthentication(new TestingAuthenticationToken("user", "pass", "ROLE_USER"));

        when(authorizationService.findByToken("unknown-token", OAuth2TokenType.ACCESS_TOKEN)).thenReturn(null);
        when(request.getSession(false)).thenReturn(null);
        when(request.getCookies()).thenReturn(null);

        ResponseEntity<Void> result = controller.logout(request, response, "unknown-token");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(authorizationService, never()).remove(any());
    }
}
