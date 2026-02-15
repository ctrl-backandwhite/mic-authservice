package com.backandwhite.integration;

import com.backandwhite.api.dto.in.RoleDtoIn;
import com.backandwhite.api.dto.out.RoleDtoOut;
import com.backandwhite.config.BaseIntegration;
import com.backandwhite.infrastructure.db.postgres.entity.RoleEntity;
import com.backandwhite.infrastructure.db.postgres.repository.RoleJpaRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

import static com.backandwhite.provider.GroupProvider.ADMIN_UNIQUE_NAME;
import static com.backandwhite.provider.RoleProvider.*;
import static org.assertj.core.api.Assertions.assertThat;

class RoleControllerIT extends BaseIntegration {

    private static final String PATH_ROLES = "/api/v1/roles";

    @Autowired
    private RoleJpaRepositoryAdapter roleRepository;

    @Test
    void create() {
        RoleDtoIn dtoIn = adminRoleDtoIn();

        RoleDtoOut response = webTestClient
                .post()
                .uri(PATH_ROLES)
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dtoIn)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(RoleDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(adminRoleDtoOut(null));
    }

    @Test
    void findAll() {
        roleRepository.saveAll(List.of(
                adminRoleEntity().withId(null),
                userRoleEntity().withId(null)));

        List<RoleDtoOut> response = webTestClient
                .get()
                .uri(PATH_ROLES)
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(RoleDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(List.of(
                        adminRoleDtoOut(null),
                        userRoleDtoOut(null)));
    }

    @Test
    void getById() {
        RoleEntity saved = roleRepository
                .save(roleEntity().withId(null));

        RoleDtoOut response = webTestClient
                .get()
                .uri(PATH_ROLES + "/" + saved.getId())
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(RoleDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(supportRoleDtoOut(saved.getId()));
    }

    @Test
    void update() {
        RoleEntity saved = roleRepository
                .save(adminRoleEntity().withId(null));
        RoleDtoIn updateDto = supportRoleDtoIn();

        RoleDtoOut response = webTestClient
                .put()
                .uri(PATH_ROLES + "/" + saved.getId())
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateDto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(RoleDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(supportRoleDtoOut(saved.getId()));
    }

    @Test
    void delete() {
        RoleEntity saved = roleRepository
                .save(adminRoleEntity().withId(null));
        
        webTestClient
                .delete()
                .uri(PATH_ROLES + "/" + saved.getId())
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                .exchange()
                .expectStatus().isNoContent();

        assertThat(roleRepository.existsById(saved.getId())).isFalse();
    }

    @Test
    void delete_notFound() {
        webTestClient
                .delete()
                .uri(PATH_ROLES + "/99999")
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                .exchange()
                .expectStatus().isNotFound();
    }
}
