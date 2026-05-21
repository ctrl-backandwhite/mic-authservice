package com.backandwhite.application.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backandwhite.application.port.out.AuthEventPort;
import com.backandwhite.application.port.out.NotificationEventPort;
import com.backandwhite.domain.repository.RoleRepository;
import com.backandwhite.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
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
    private RoleRepository roleRepository;

    @MockitoBean
    private AuthEventPort authEventPort;

    @MockitoBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @MockitoBean
    private JdbcOperations jdbcOperations;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void logout_authenticated_acceptJson_returnsNoContent() throws Exception {
        // Post-logout handler returns 204 when the caller requests JSON, and
        // otherwise falls back to the default browser redirect flow.
        mockMvc.perform(post("/logout").header("Accept", "application/json")).andExpect(status().isNoContent());
    }

    @Test
    void logout_unauthenticated_redirectsToHome() throws Exception {
        // Without an Accept: application/json header the post-logout handler
        // delegates to SimpleUrlLogoutSuccessHandler, which 302-redirects to "/".
        mockMvc.perform(post("/logout")).andExpect(status().is3xxRedirection());
    }
}
