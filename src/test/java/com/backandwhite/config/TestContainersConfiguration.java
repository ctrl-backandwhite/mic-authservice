package com.backandwhite.config;

import com.backandwhite.core.test.JwtTestUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(value = "authTestContainersConfiguration", proxyBeanMethods = false)
public class TestContainersConfiguration {

    @Value("${test.postgres.username}")
    private String username;
    @Value("${test.postgres.password}")
    private String password;
    @Value("${test.postgres.database}")
    private String dbName;

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest")).withUsername(username)
                .withPassword(password).withDatabaseName(dbName);
    }

    @Bean
    @Primary
    JwtTestUtil jwtTestUtil(JwtEncoder jwtEncoder) {
        return new JwtTestUtil(jwtEncoder, MacAlgorithm.HS256);
    }
}
