package com.backandwhite.application.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@ExtendWith(MockitoExtension.class)
class WebMvcConfigTest {

    @Mock
    private SessionValidationInterceptor sessionValidationInterceptor;

    @InjectMocks
    private WebMvcConfig webMvcConfig;

    @Test
    void addInterceptors_registersSessionInterceptor() {
        InterceptorRegistry registry = mock(InterceptorRegistry.class);
        InterceptorRegistration registration = mock(InterceptorRegistration.class);

        when(registry.addInterceptor(sessionValidationInterceptor)).thenReturn(registration);
        when(registration.addPathPatterns(any(String.class))).thenReturn(registration);

        webMvcConfig.addInterceptors(registry);

        verify(registry).addInterceptor(sessionValidationInterceptor);
        verify(registration).addPathPatterns("/api/v1/**");
        verify(registration).excludePathPatterns("/api/v1/auth/logout");
    }
}
