package com.backandwhite;

import com.backandwhite.common.configuration.annotation.EnabledCoreFinolApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnabledCoreFinolApplication
@OpenAPIDefinition(servers = {
        @Server(url = "https://mic-auth-production.up.railway.app/", description = "Production Server."),
        @Server(url = "http://localhost:9001", description = "Local Server.")
})
public class MicAuthserviceApplication {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static void main(String[] args) {
        SpringApplication.run(MicAuthserviceApplication.class, args);
    }
}
