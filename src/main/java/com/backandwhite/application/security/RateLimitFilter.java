package com.backandwhite.application.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * In-memory rate limiter for sensitive public endpoints
 * (registration, password-reset, login).
 * <p>
 * Uses a sliding-window counter per client IP + path group.
 * Stale entries are evicted lazily on each request check.
 */
@Log4j2
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 5;
    private static final long WINDOW_MS = 15 * 60 * 1000L; // 15 minutes

    private static final int LOGIN_MAX_REQUESTS = 10;
    private static final long LOGIN_WINDOW_MS = 5 * 60 * 1000L; // 5 minutes

    /** IP+bucket → deque of request timestamps */
    private final Map<String, Deque<Long>> requestCounts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        RateConfig config = resolveRateConfig(method, path);
        if (config == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        String bucketKey = clientIp + ":" + config.bucket;

        if (isRateLimited(bucketKey, config.maxRequests, config.windowMs)) {
            log.warn("::> Rate limit exceeded for IP={} on {}", clientIp, path);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Too many requests. Please try again later.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isRateLimited(String key, int maxRequests, long windowMs) {
        long now = System.currentTimeMillis();
        Deque<Long> timestamps = requestCounts.computeIfAbsent(key, k -> new ConcurrentLinkedDeque<>());

        // Evict expired entries
        while (!timestamps.isEmpty() && now - timestamps.peekFirst() > windowMs) {
            timestamps.pollFirst();
        }

        if (timestamps.size() >= maxRequests) {
            return true;
        }

        timestamps.addLast(now);
        return false;
    }

    private RateConfig resolveRateConfig(String method, String path) {
        if (HttpMethod.POST.matches(method)) {
            if (path.endsWith("/register")) {
                return new RateConfig("register", MAX_REQUESTS, WINDOW_MS);
            }
            if (path.endsWith("/forgot-password")) {
                return new RateConfig("forgot-password", MAX_REQUESTS, WINDOW_MS);
            }
            if (path.endsWith("/reset-password")) {
                return new RateConfig("reset-password", MAX_REQUESTS, WINDOW_MS);
            }
            if (path.equals("/login")) {
                return new RateConfig("login", LOGIN_MAX_REQUESTS, LOGIN_WINDOW_MS);
            }
        }
        return null;
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private record RateConfig(String bucket, int maxRequests, long windowMs) {
    }
}
