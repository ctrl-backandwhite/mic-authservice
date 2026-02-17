package com.backandwhite.application.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom authentication failure handler that provides detailed error information
 * and proper logging for debugging authentication issues.
 */
@Log4j2
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        
        log.error("::> ========================================");
        log.error("::> Authentication failure for request from: {}", request.getRemoteAddr());
        log.error("::> Request URI: {}", request.getRequestURI());
        log.error("::> Session ID: {}", request.getSession(false) != null ? request.getSession(false).getId() : "No session");
        log.error("::> Exception type: {}", exception.getClass().getSimpleName());
        log.error("::> Exception message: {}", exception.getMessage());
        log.error("::> Full exception: ", exception);
        log.error("::> ========================================");

        // Determine the error message based on exception type
        String errorMessage = "Authentication failed";
        String errorCode = "AUTH_ERROR";

        if (exception instanceof BadCredentialsException) {
            errorMessage = "Invalid username or password";
            errorCode = "BAD_CREDENTIALS";
        } else if (exception instanceof UsernameNotFoundException) {
            errorMessage = "User not found";
            errorCode = "USER_NOT_FOUND";
        } else if (exception instanceof DisabledException) {
            errorMessage = "Account is disabled";
            errorCode = "ACCOUNT_DISABLED";
        } else if (exception instanceof LockedException) {
            errorMessage = "Account is locked";
            errorCode = "ACCOUNT_LOCKED";
        }

        // Build error response
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", ZonedDateTime.now().toString());
        errorResponse.put("status", HttpStatus.UNAUTHORIZED.value());
        errorResponse.put("error", errorMessage);
        errorResponse.put("code", errorCode);
        errorResponse.put("path", request.getRequestURI());

        // Check if request expects JSON response
        String acceptHeader = request.getHeader("Accept");
        boolean isJsonRequest = acceptHeader != null && acceptHeader.contains("application/json");

        if (isJsonRequest) {
            // Return JSON error response
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } else {
            // Redirect to login page with error parameter
            String redirectUrl = "/login?error=true&message=" + errorCode;
            log.info("::> Redirecting to: {}", redirectUrl);
            response.sendRedirect(redirectUrl);
        }
    }
}
