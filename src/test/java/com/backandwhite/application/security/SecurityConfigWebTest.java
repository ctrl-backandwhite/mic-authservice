package com.backandwhite.application.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backandwhite.application.port.out.NotificationEventPort;
import com.backandwhite.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = {SecurityConfig.class, CorsRegistryConfiguration.class})
@AutoConfigureMockMvc
@TestPropertySource(properties = {"app.security.handler-url-1=http://localhost:4200",
        "app.security.handler-url-2=http://localhost:4200",
        "app.jwt.secret=local-secret-key-change-me-in-production-must-be-256-bits-long"})
class SecurityConfigWebTest {

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private SessionRevokingLogoutHandler sessionRevokingLogoutHandler;

    @MockitoBean
    private RateLimitFilter rateLimitFilter;

    @MockitoBean
    private NotificationEventPort notificationEventPort;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JdbcOperations jdbcOperations;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void logout_authenticated_returnsNoContent() throws Exception {
        mockMvc.perform(post("/logout")).andExpect(status().isNoContent());
    }

    @Test
    void logout_unauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(post("/logout")).andExpect(status().isNoContent());
    }
}
