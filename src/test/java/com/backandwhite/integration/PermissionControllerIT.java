package com.backandwhite.integration;

import static com.backandwhite.provider.PermissionProvider.*;
import static com.backandwhite.provider.RoleProvider.ADMIN_UNIQUE_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.api.dto.in.PermissionDtoIn;
import com.backandwhite.api.dto.out.PermissionDtoOut;
import com.backandwhite.config.BaseIntegration;
import com.backandwhite.infrastructure.db.postgres.entity.PermissionEntity;
import com.backandwhite.infrastructure.db.postgres.repository.PermissionJpaRepositoryAdapter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

class PermissionControllerIT extends BaseIntegration {
    private static final String PATH = "/api/v1/permissions";

    @Autowired
    private PermissionJpaRepositoryAdapter permissionRepository;

    @Test
    void create() {
        PermissionDtoIn dtoIn = permissionDtoIn();
        PermissionDtoOut response = webTestClient.post().uri(PATH)
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                .contentType(MediaType.APPLICATION_JSON).bodyValue(dtoIn).exchange().expectStatus().isCreated()
                .expectBody(PermissionDtoOut.class).returnResult().getResponseBody();
        assertThat(response).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy", ".*updatedBy")
                .ignoringFields("id").isEqualTo(permissionDtoOut(null));
    }

    @Test
    void findAll() {
        permissionRepository.saveAll(List.of(permissionEntity().withId(null)));
        List<PermissionDtoOut> response = webTestClient.get().uri(PATH)
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME))).exchange().expectStatus()
                .isOk().expectBodyList(PermissionDtoOut.class).returnResult().getResponseBody();
        assertThat(response).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy", ".*updatedBy")
                .ignoringFields("id").isEqualTo(List.of(permissionDtoOut(null)));
    }

    @Test
    void getById() {
        PermissionEntity saved = permissionRepository.save(permissionEntity().withId(null));
        PermissionDtoOut response = webTestClient.get().uri(PATH + "/" + saved.getId())
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME))).exchange().expectStatus()
                .isOk().expectBody(PermissionDtoOut.class).returnResult().getResponseBody();
        assertThat(response).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy", ".*updatedBy")
                .isEqualTo(permissionDtoOut(saved.getId()));
    }

    @Test
    void update() {
        PermissionEntity saved = permissionRepository.save(permissionEntity().withId(null));
        PermissionDtoIn updateDto = permissionDtoIn();
        PermissionDtoOut response = webTestClient.put().uri(PATH + "/" + saved.getId())
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                .contentType(MediaType.APPLICATION_JSON).bodyValue(updateDto).exchange().expectStatus().isOk()
                .expectBody(PermissionDtoOut.class).returnResult().getResponseBody();
        assertThat(response).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy", ".*updatedBy")
                .isEqualTo(permissionDtoOut(saved.getId()));
    }

    @Test
    void delete() {
        PermissionEntity saved = permissionRepository.save(permissionEntity().withId(null));
        webTestClient.delete().uri(PATH + "/" + saved.getId())
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME))).exchange().expectStatus()
                .isNoContent();
        assertThat(permissionRepository.existsById(saved.getId())).isFalse();
    }

    @Test
    void delete_notFound() {
        webTestClient.delete().uri(PATH + "/99999")
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME))).exchange().expectStatus()
                .isNotFound();
    }
}
