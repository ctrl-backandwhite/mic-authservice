package com.backandwhite.application.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.backandwhite.infrastructure.db.postgres.entity.UserSessionEntity;
import com.backandwhite.infrastructure.db.postgres.repository.UserSessionJpaRepositoryAdapter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

@ExtendWith(MockitoExtension.class)
class SessionValidationInterceptorTest {

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private UserSessionJpaRepositoryAdapter sessionJpaRepository;

    @InjectMocks
    private SessionValidationInterceptor interceptor;

    @Test
    void preHandle_noAuthHeader_allowsRequest() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
        verifyNoInteractions(jwtDecoder);
    }

    @Test
    void preHandle_nonBearerAuth_allowsRequest() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic abc123");

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
        verifyNoInteractions(jwtDecoder);
    }

    @Test
    void preHandle_validSessionNotRevoked_allowsRequest() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        String token = "valid.jwt.token";
        Jwt jwt = Jwt.withTokenValue(token).header("alg", "HS256").claim("sid", "session-123").build();

        UserSessionEntity session = UserSessionEntity.builder().sessionId("session-123").revoked(false).build();

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtDecoder.decode(token)).thenReturn(jwt);
        when(sessionJpaRepository.findBySessionId("session-123")).thenReturn(session);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
    }

    @Test
    void preHandle_revokedSession_blocks() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        String token = "valid.jwt.token";
        Jwt jwt = Jwt.withTokenValue(token).header("alg", "HS256").claim("sid", "session-revoked").build();

        UserSessionEntity session = UserSessionEntity.builder().sessionId("session-revoked").revoked(true).build();

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtDecoder.decode(token)).thenReturn(jwt);
        when(sessionJpaRepository.findBySessionId("session-revoked")).thenReturn(session);
        when(response.getWriter()).thenReturn(pw);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isFalse();
        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void preHandle_jwtDecodeFailure_allowsRequest() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        String token = "invalid.token";

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtDecoder.decode(token)).thenThrow(new JwtException("Bad token"));

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
        verifyNoInteractions(sessionJpaRepository);
    }

    @Test
    void preHandle_noSessionClaim_allowsRequest() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        String token = "valid.jwt.token";
        Jwt jwt = Jwt.withTokenValue(token).header("alg", "HS256").claim("sub", "user").build();

        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
        when(jwtDecoder.decode(token)).thenReturn(jwt);

        boolean result = interceptor.preHandle(request, response, new Object());

        assertThat(result).isTrue();
        verifyNoInteractions(sessionJpaRepository);
    }
}
