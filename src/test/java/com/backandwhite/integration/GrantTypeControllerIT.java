package com.backandwhite.integration;

import static com.backandwhite.provider.GrantTypeProvider.*;
import static com.backandwhite.provider.RoleProvider.ADMIN_UNIQUE_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.api.dto.in.GrantTypeDtoIn;
import com.backandwhite.api.dto.out.GrantTypeDtoOut;
import com.backandwhite.config.BaseIntegration;
import com.backandwhite.infrastructure.db.postgres.entity.GrantTypeEntity;
import com.backandwhite.infrastructure.db.postgres.repository.GrantTypeJpaRepositoryAdapter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

class GrantTypeControllerIT extends BaseIntegration {

    private static final String PATH = "/api/v1/granttypes";

    @Autowired
    private GrantTypeJpaRepositoryAdapter repository;

    @Test
    void create() {
        GrantTypeDtoIn dtoIn = grantTypeDtoIn();

        GrantTypeDtoOut response = webTestClient.post().uri(PATH)
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                .contentType(MediaType.APPLICATION_JSON).bodyValue(dtoIn).exchange().expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody(GrantTypeDtoOut.class).returnResult()
                .getResponseBody();

        assertThat(response).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy", ".*updatedBy")
                .ignoringFields("id").isEqualTo(grantTypeDtoOut(null));
    }

    @Test
    void findAll() {
        repository.saveAll(List.of(grantTypeEntity().withId(null), otherGrantTypeEntity().withId(null)));

        List<GrantTypeDtoOut> response = webTestClient.get().uri(PATH)
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME))).exchange().expectStatus()
                .isOk().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBodyList(GrantTypeDtoOut.class)
                .returnResult().getResponseBody();

        assertThat(response).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy", ".*updatedBy")
                .ignoringFields("id").isEqualTo(List.of(grantTypeDtoOut(null), otherGrantTypeDtoOut(null)));
    }

    @Test
    void getById() {
        GrantTypeEntity saved = repository.save(grantTypeEntity().withId(null));

        GrantTypeDtoOut response = webTestClient.get().uri(PATH + "/" + saved.getId())
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME))).exchange().expectStatus()
                .isOk().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody(GrantTypeDtoOut.class)
                .returnResult().getResponseBody();

        assertThat(response).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy", ".*updatedBy")
                .isEqualTo(grantTypeDtoOut(saved.getId()));
    }

    @Test
    void update() {
        GrantTypeEntity saved = repository.save(grantTypeEntity().withId(null));
        GrantTypeDtoIn updateDto = otherGrantTypeDtoIn();

        GrantTypeDtoOut response = webTestClient.put().uri(PATH + "/" + saved.getId())
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                .contentType(MediaType.APPLICATION_JSON).bodyValue(updateDto).exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody(GrantTypeDtoOut.class).returnResult()
                .getResponseBody();

        assertThat(response).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy", ".*updatedBy")
                .isEqualTo(otherGrantTypeDtoOut(saved.getId()));
    }

    @Test
    void delete() {
        GrantTypeEntity saved = repository.save(grantTypeEntity().withId(null));

        webTestClient.delete().uri(PATH + "/" + saved.getId())
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME))).exchange().expectStatus()
                .isNoContent();

        assertThat(repository.existsById(saved.getId())).isFalse();
    }

    @Test
    void delete_notFound() {
        webTestClient.delete().uri(PATH + "/99999")
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME))).exchange().expectStatus()
                .isNotFound();
    }
}
