package com.backandwhite;

import com.backandwhite.common.configuration.annotation.EnabledCoreFinolApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;

@EnabledCoreFinolApplication
@OpenAPIDefinition(servers = {
        @Server(url = "https://mic-authservice-production.up.railway.app", description = "Production Server."),
        @Server(url = "https://localhost:8443", description = "Local Server.")
})
public class MicAuthserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicAuthserviceApplication.class, args);
    }
}
