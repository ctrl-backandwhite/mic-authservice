package com.backandwhite.application.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class CorsRegistryConfigurationTest {

    @Test
    void passwordEncoder_returnsBCryptEncoder() {
        CorsRegistryConfiguration config = new CorsRegistryConfiguration();

        PasswordEncoder encoder = config.passwordEncoder();

        assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
    }
}
