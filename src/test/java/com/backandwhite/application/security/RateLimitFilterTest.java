package com.backandwhite.application.security;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    @ParameterizedTest(name = "uri={0}")
    @CsvSource({"/api/v1/auth/register,10.0.0.1", "/login,10.0.0.200", "/api/v1/auth/forgot-password,10.0.0.201",
            "/api/v1/auth/reset-password,10.0.0.202"})
    void doFilterInternal_rateLimitedEndpoint_allowsWithinLimit(String uri, String remoteAddr) throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn(uri);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRemoteAddr()).thenReturn(remoteAddr);

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
    void doFilterInternal_blankXForwardedFor_fallsBackToRemoteAddr() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/api/v1/auth/register");
        when(request.getMethod()).thenReturn("POST");
        when(request.getHeader("X-Forwarded-For")).thenReturn("   ");
        when(request.getRemoteAddr()).thenReturn("10.0.0.55");

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_evictsExpiredTimestamps() throws Exception {
        // Inject a stale timestamp into the filter's internal map so the
        // eviction loop in isRateLimited has something to drain.
        java.util.Map<String, java.util.Deque<Long>> counts = (java.util.Map<String, java.util.Deque<Long>>) org.springframework.test.util.ReflectionTestUtils
                .getField(rateLimitFilter, "requestCounts");
        java.util.Deque<Long> stale = new java.util.concurrent.ConcurrentLinkedDeque<>();
        // Window for /register is 15 minutes -> insert timestamp 1 hour ago.
        stale.add(System.currentTimeMillis() - 60L * 60L * 1000L);
        counts.put("10.0.0.77:register", stale);

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/api/v1/auth/register");
        when(request.getMethod()).thenReturn("POST");
        when(request.getRemoteAddr()).thenReturn("10.0.0.77");

        rateLimitFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        // Stale entry should be drained leaving exactly one new timestamp.
        org.assertj.core.api.Assertions.assertThat(counts.get("10.0.0.77:register")).hasSize(1);
    }
}
