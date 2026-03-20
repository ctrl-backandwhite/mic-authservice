package com.backandwhite.application.security;

import com.backandwhite.domain.repository.OauthClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = { SecurityConfig.class, CorsRegistryConfiguration.class })
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "app.security.handler-url-1=http://localhost:4200",
        "app.security.handler-url-2=http://localhost:4200"
})
class SecurityConfigWebTest {

    @MockitoBean
    private OauthClientRepository oauthClientRepository;

    @MockitoBean
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    void logout_authenticated_returnsNoContent() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().isNoContent());
    }

    @Test
    void logout_unauthenticated_returnsNoContent() throws Exception {
        mockMvc.perform(post("/logout"))
                .andExpect(status().isNoContent());
    }
}
