package com.backandwhite;

import com.backandwhite.common.configuration.annotation.EnabledCoreFinolApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnabledCoreFinolApplication
public class MicAuthserviceApplication {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        SpringApplication.run(MicAuthserviceApplication.class, args);
    }
}
