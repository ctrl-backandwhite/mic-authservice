package com.backandwhite;

import com.backandwhite.common.configuration.annotation.EnableCoreApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;

@EnableCoreApplication
@OpenAPIDefinition(servers = {
        //@Server(url = "https://mic-authservice-production.up.railway.app", description = "Production Server."),
        @Server(url = "http://localhost:6001", description = "Local Server.")
})
public class MicAuthserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicAuthserviceApplication.class, args);
    }
}
