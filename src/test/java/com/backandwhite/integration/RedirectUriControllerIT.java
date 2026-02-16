package com.backandwhite.integration;

import com.backandwhite.api.dto.in.RedirectUriDtoIn;
import com.backandwhite.api.dto.out.RedirectUriDtoOut;
import com.backandwhite.config.BaseIntegration;
import com.backandwhite.infrastructure.db.postgres.entity.RedirectUriEntity;
import com.backandwhite.infrastructure.db.postgres.repository.RedirectUriJpaRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

import static com.backandwhite.provider.RedirectUriProvider.*;
import static com.backandwhite.provider.RoleProvider.ADMIN_UNIQUE_NAME;
import static org.assertj.core.api.Assertions.assertThat;

class RedirectUriControllerIT extends BaseIntegration {

        private static final String PATH = "/api/v1/redirecturis";

        @Autowired
        private RedirectUriJpaRepositoryAdapter repository;

        @Test
        void create() {
                RedirectUriDtoIn dtoIn = redirectUriDtoIn();

                RedirectUriDtoOut response = webTestClient
                                .post()
                                .uri(PATH)
                                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(dtoIn)
                                .exchange()
                                .expectStatus().isCreated()
                                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                .expectBody(RedirectUriDtoOut.class)
                                .returnResult()
                                .getResponseBody();

                assertThat(response)
                                .usingRecursiveComparison()
                                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy",
                                                ".*updatedBy")
                                .ignoringFields("id")
                                .isEqualTo(redirectUriDtoOut(null));
        }

        @Test
        void findAll() {
                repository.saveAll(List.of(
                                redirectUriEntity().withId(null),
                                otherRedirectUriEntity().withId(null)));

                List<RedirectUriDtoOut> response = webTestClient
                                .get()
                                .uri(PATH)
                                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                .expectBodyList(RedirectUriDtoOut.class)
                                .returnResult()
                                .getResponseBody();

                assertThat(response)
                                .usingRecursiveComparison()
                                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy",
                                                ".*updatedBy")
                                .ignoringFields("id")
                                .isEqualTo(List.of(
                                                redirectUriDtoOut(null),
                                                otherRedirectUriDtoOut(null)));
        }

        @Test
        void getById() {
                RedirectUriEntity saved = repository.save(redirectUriEntity().withId(null));

                RedirectUriDtoOut response = webTestClient
                                .get()
                                .uri(PATH + "/" + saved.getId())
                                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                .expectBody(RedirectUriDtoOut.class)
                                .returnResult()
                                .getResponseBody();

                assertThat(response)
                                .usingRecursiveComparison()
                                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy",
                                                ".*updatedBy")
                                .isEqualTo(redirectUriDtoOut(saved.getId()));
        }

        @Test
        void update() {
                RedirectUriEntity saved = repository.save(redirectUriEntity().withId(null));
                RedirectUriDtoIn updateDto = otherRedirectUriDtoIn();

                RedirectUriDtoOut response = webTestClient
                                .put()
                                .uri(PATH + "/" + saved.getId())
                                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(updateDto)
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                .expectBody(RedirectUriDtoOut.class)
                                .returnResult()
                                .getResponseBody();

                assertThat(response)
                                .usingRecursiveComparison()
                                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy",
                                                ".*updatedBy")
                                .isEqualTo(otherRedirectUriDtoOut(saved.getId()));
        }

        @Test
        void delete() {
                RedirectUriEntity saved = repository.save(redirectUriEntity().withId(null));

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
