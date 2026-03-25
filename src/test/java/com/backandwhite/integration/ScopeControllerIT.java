package com.backandwhite.integration;

import com.backandwhite.api.dto.in.ScopeDtoIn;
import com.backandwhite.api.dto.out.ScopeDtoOut;
import com.backandwhite.config.BaseIntegration;
import com.backandwhite.infrastructure.db.postgres.entity.ScopeEntity;
import com.backandwhite.infrastructure.db.postgres.repository.ScopeJpaRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

import static com.backandwhite.provider.RoleProvider.ADMIN_UNIQUE_NAME;
import static com.backandwhite.provider.ScopeProvider.*;
import static org.assertj.core.api.Assertions.assertThat;

class ScopeControllerIT extends BaseIntegration {

        private static final String PATH = "/api/v1/scopes";

        @Autowired
        private ScopeJpaRepositoryAdapter repository;

        @Test
        void create() {
                ScopeDtoIn dtoIn = readScopeDtoIn();

                ScopeDtoOut response = webTestClient
                                .post()
                                .uri(PATH)
                                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(dtoIn)
                                .exchange()
                                .expectStatus().isCreated()
                                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                .expectBody(ScopeDtoOut.class)
                                .returnResult()
                                .getResponseBody();

                assertThat(response)
                                .usingRecursiveComparison()
                                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy",
                                                ".*updatedBy")
                                .ignoringFields("id")
                                .isEqualTo(readScopeDtoOut(null));
        }

        @Test
        void findAll() {
                repository.saveAll(List.of(
                                readScopeEntity().withId(null),
                                writeScopeEntity().withId(null)));

                List<ScopeDtoOut> response = webTestClient
                                .get()
                                .uri(PATH)
                                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                .expectBodyList(ScopeDtoOut.class)
                                .returnResult()
                                .getResponseBody();

                assertThat(response)
                                .usingRecursiveComparison()
                                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy",
                                                ".*updatedBy")
                                .ignoringFields("id")
                                .isEqualTo(List.of(
                                                readScopeDtoOut(null),
                                                writeScopeDtoOut(null)));
        }

        @Test
        void getById() {
                ScopeEntity saved = repository.save(readScopeEntity().withId(null));

                ScopeDtoOut response = webTestClient
                                .get()
                                .uri(PATH + "/" + saved.getId())
                                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                .expectBody(ScopeDtoOut.class)
                                .returnResult()
                                .getResponseBody();

                assertThat(response)
                                .usingRecursiveComparison()
                                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy",
                                                ".*updatedBy")
                                .isEqualTo(readScopeDtoOut(saved.getId()));
        }

        @Test
        void update() {
                ScopeEntity saved = repository.save(readScopeEntity().withId(null));
                ScopeDtoIn updateDto = writeScopeDtoIn();

                ScopeDtoOut response = webTestClient
                                .put()
                                .uri(PATH + "/" + saved.getId())
                                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(updateDto)
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                .expectBody(ScopeDtoOut.class)
                                .returnResult()
                                .getResponseBody();

                assertThat(response)
                                .usingRecursiveComparison()
                                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy",
                                                ".*updatedBy")
                                .isEqualTo(writeScopeDtoOut(saved.getId()));
        }

        @Test
        void delete() {
                ScopeEntity saved = repository.save(readScopeEntity().withId(null));

                webTestClient
                                .delete()
                                .uri(PATH + "/" + saved.getId())
                                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                                .exchange()
                                .expectStatus().isNoContent();

                assertThat(repository.existsById(saved.getId())).isFalse();
        }

        @Test
        void delete_notFound() {
                webTestClient
                                .delete()
                                .uri(PATH + "/99999")
                                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                                .exchange()
                                .expectStatus().isNotFound();
        }
}
