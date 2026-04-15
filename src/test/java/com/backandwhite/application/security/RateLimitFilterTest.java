package com.backandwhite.application.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class RateLimitFilterTest {

    private RateLimitFilter rateLimitFilter;

    @BeforeEach
    void setUp() {
        rateLimitFilter = new RateLimitFilter();
    }

    @Test
    void doFilterInternal_nonRateLimitedPath_passesThrough() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/api/v1/users");
        when(request.getMethod()).thenReturn("GET");

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_registerEndpoint_allowsWithinLimit() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/api/v1/auth/register");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_exceedsLimit_returns429() throws Exception {
        // Send 6 requests from the same IP to /register (limit is 5)
        for (int i = 0; i < 5; i++) {
            HttpServletRequest req = mock(HttpServletRequest.class);
            HttpServletResponse resp = mock(HttpServletResponse.class);
            FilterChain chain = mock(FilterChain.class);

            when(req.getRequestURI()).thenReturn("/api/v1/auth/register");
            when(req.getMethod()).thenReturn("POST");
            when(req.getRemoteAddr()).thenReturn("10.0.0.99");

            rateLimitFilter.doFilterInternal(req, resp, chain);
        }

        // 6th request should be blocked
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        when(request.getRequestURI()).thenReturn("/api/v1/auth/register");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRemoteAddr()).thenReturn("10.0.0.99");
        when(response.getWriter()).thenReturn(pw);

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    }

    @Test
    void doFilterInternal_usesXForwardedForHeader() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/api/v1/auth/register");
        when(request.getMethod()).thenReturn("POST");
        when(request.getHeader("X-Forwarded-For")).thenReturn("1.2.3.4, 5.6.7.8");

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_loginEndpoint_rateLimited() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/login");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRemoteAddr()).thenReturn("10.0.0.200");

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_forgotPasswordEndpoint_rateLimited() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/api/v1/auth/forgot-password");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRemoteAddr()).thenReturn("10.0.0.201");

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
