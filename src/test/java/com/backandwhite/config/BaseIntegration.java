package com.backandwhite.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Log4j2
@Testcontainers
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@Import(TestContainersConfiguration.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegration {

    public static final String PREFIX = "hibernate_";
    private static final Pattern VALID_TABLE_NAME_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");
    private static final Set<String> EXCLUDED_TABLE_PREFIXES = Set.of("hibernate_", "flyway_", "liquibase_");

    @Autowired
    private JwtTestUtil jwtTestUtil;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @LocalServerPort
    private int port;

    protected WebTestClient webTestClient;

    static {
        org.testcontainers.utility.TestcontainersConfiguration.getInstance()
                .updateUserConfig("checks.disable", "true");
    }

    public String getToken(List<String> roles) {
        return jwtTestUtil.getToken("John Doe", roles);
    }

    @BeforeEach
    @Transactional
    public void cleanAllTables() {
        if (webTestClient == null) {
            webTestClient = WebTestClient.bindToServer()
                    .baseUrl("http://localhost:" + port)
                    .build();
        }
        log.info("Iniciando limpieza de todas las tablas y reiniciando IDs...");

        List<String> tableNames;
        try {

            tableNames = jdbcTemplate.queryForList(
                    "SELECT tablename FROM pg_tables WHERE schemaname = 'public'", String.class);

        } catch (Exception e) {
            log.error("Error al obtener los nombres de las tablas de la base de datos: {}", e.getMessage());
            throw new IllegalStateException("No se pudieron obtener los nombres de las tablas para la limpieza.", e);
        }

        List<String> tablesToTruncate = tableNames.stream()
                .filter(tableName -> EXCLUDED_TABLE_PREFIXES.stream().noneMatch(tableName::startsWith))
                .filter(tableName -> {
                    if (!VALID_TABLE_NAME_PATTERN.matcher(tableName).matches()) {
                        log.warn(
                                "ADVERTENCIA DE SEGURIDAD: El nombre de la tabla '{}' contiene caracteres inválidos. No se truncará para prevenir SQL Injection.",
                                tableName);
                        return false;
                    }
                    return true;
                })
                .toList();

        if (tablesToTruncate.isEmpty()) {
            log.info("No se encontraron tablas para truncar después de la filtración y validación.");
            return;
        }

        for (String tableName : tablesToTruncate) {
            try {
                String truncateSql = "TRUNCATE TABLE ".concat(tableName).concat(" RESTART IDENTITY CASCADE");
                log.info("Ejecutando: {}", truncateSql);
                jdbcTemplate.execute(truncateSql);
            } catch (Exception e) {
                log.error("Error al truncar la tabla '{}': {}", tableName, e.getMessage());
            }
        }
        log.info("Limpieza de tablas finalizada.");
    }
}
