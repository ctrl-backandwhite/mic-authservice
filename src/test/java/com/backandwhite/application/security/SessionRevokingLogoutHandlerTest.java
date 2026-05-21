package com.backandwhite.application.security;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.backandwhite.domain.repository.UserSessionRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

@ExtendWith(MockitoExtension.class)
class SessionRevokingLogoutHandlerTest {

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private UserSessionRepository userSessionRepository;

    @InjectMocks
    private SessionRevokingLogoutHandler handler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Test
    void logout_withValidBearerToken_revokesSession() {
        String token = "valid.jwt.token";
        Jwt jwt = Jwt.withTokenValue(token).header("alg", "HS256").claim("sid", "session-123").build();

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        handler.logout(request, response, authentication);

        verify(userSessionRepository).revokeSession("session-123");
    }

    @Test
    void logout_withoutAuthorizationHeader_skipsRevocation() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        handler.logout(request, response, authentication);

        verifyNoInteractions(jwtDecoder);
        verifyNoInteractions(userSessionRepository);
    }

    @Test
    void logout_withNonBearerAuth_skipsRevocation() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic abc123");

        handler.logout(request, response, authentication);

        verifyNoInteractions(jwtDecoder);
        verifyNoInteractions(userSessionRepository);
    }

    @Test
    void logout_jwtDecodeFailure_doesNotThrow() {
        String token = "invalid.jwt.token";

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtDecoder.decode(token)).thenThrow(new JwtException("Failed"));

        handler.logout(request, response, authentication);

        verifyNoInteractions(userSessionRepository);
    }

    @Test
    void logout_withNullSessionId_doesNotRevoke() {
        String token = "valid.jwt.token";
        Jwt jwt = Jwt.withTokenValue(token).header("alg", "HS256").claim("sub", "user").build();

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        handler.logout(request, response, authentication);

        verifyNoInteractions(userSessionRepository);
    }

    @Test
    void logout_withBlankSessionId_doesNotRevoke() {
        String token = "valid.jwt.token";
        Jwt jwt = Jwt.withTokenValue(token).header("alg", "HS256").claim("sid", "   ").build();

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        handler.logout(request, response, authentication);

        verifyNoInteractions(userSessionRepository);
    }
}
