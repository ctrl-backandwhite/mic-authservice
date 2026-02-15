package com.backandwhite.integration;

import com.backandwhite.api.dto.in.OauthClientDtoIn;
import com.backandwhite.api.dto.out.OauthClientDtoOut;
import com.backandwhite.config.BaseIntegration;
import com.backandwhite.infrastructure.db.postgres.entity.GrantTypeEntity;
import com.backandwhite.infrastructure.db.postgres.entity.OauthClientEntity;
import com.backandwhite.infrastructure.db.postgres.entity.RedirectUriEntity;
import com.backandwhite.infrastructure.db.postgres.entity.ScopeEntity;
import com.backandwhite.infrastructure.db.postgres.repository.GrantTypeJpaRepositoryAdapter;
import com.backandwhite.infrastructure.db.postgres.repository.OauthClientJpaRepositoryAdapter;
import com.backandwhite.infrastructure.db.postgres.repository.RedirectUriJpaRepositoryAdapter;
import com.backandwhite.infrastructure.db.postgres.repository.ScopeJpaRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

import static com.backandwhite.provider.GrantTypeProvider.grantTypeEntity;
import static com.backandwhite.provider.OauthClientProvider.*;
import static com.backandwhite.provider.RedirectUriProvider.redirectUriEntity;
import static com.backandwhite.provider.RoleProvider.ADMIN_UNIQUE_NAME;
import static com.backandwhite.provider.ScopeProvider.readScopeEntity;
import static org.assertj.core.api.Assertions.assertThat;

class OauthClientControllerIT extends BaseIntegration {

    private static final String PATH = "/api/v1/oauthclients";

    @Autowired
    private OauthClientJpaRepositoryAdapter repository;

    @Autowired
    private ScopeJpaRepositoryAdapter scopeRepository;

    @Autowired
    private RedirectUriJpaRepositoryAdapter redirectUriRepository;

    @Autowired
    private GrantTypeJpaRepositoryAdapter grantTypeRepository;

    @Test
    void create() {
        OauthClientDtoIn dtoIn = oauthClientDtoIn().withScopeIds(List.of(seedScope().getId()))
                .withRedirectUriIds(List.of(seedRedirectUri().getId()))
                .withGrantTypeIds(List.of(seedGrantType().getId()));

        OauthClientDtoOut response = webTestClient
                .post()
                .uri(PATH)
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dtoIn)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(OauthClientDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("id", "scopes", "redirectUris", "grantTypes")
                .isEqualTo(oauthClientDtoOut(null));
    }

    @Test
    void findAll() {
        ScopeEntity scope = seedScope();
        RedirectUriEntity redirectUri = seedRedirectUri();
        GrantTypeEntity grantType = seedGrantType();

        repository.saveAll(List.of(
                oauthClientEntity().withId(null)
                        .withScopes(List.of(scope))
                        .withRedirectUris(List.of(redirectUri))
                        .withGrantTypes(List.of(grantType)),
                otherOauthClientEntity().withId(null)
                        .withScopes(List.of(scope))
                        .withRedirectUris(List.of(redirectUri))
                        .withGrantTypes(List.of(grantType))));

        List<OauthClientDtoOut> response = webTestClient
                .get()
                .uri(PATH)
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(OauthClientDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("id", "scopes", "redirectUris", "grantTypes")
                .isEqualTo(List.of(
                        oauthClientDtoOut(null),
                        otherOauthClientDtoOut(null)));
    }

    @Test
    void getById() {
        ScopeEntity scope = seedScope();
        RedirectUriEntity redirectUri = seedRedirectUri();
        GrantTypeEntity grantType = seedGrantType();

        OauthClientEntity saved = repository.save(oauthClientEntity().withId(null)
                .withScopes(List.of(scope))
                .withRedirectUris(List.of(redirectUri))
                .withGrantTypes(List.of(grantType)));

        OauthClientDtoOut response = webTestClient
                .get()
                .uri(PATH + "/" + saved.getId())
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(OauthClientDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("scopes", "redirectUris", "grantTypes")
                .isEqualTo(oauthClientDtoOut(saved.getId()));
    }

    @Test
    void update() {
        ScopeEntity scope = seedScope();
        RedirectUriEntity redirectUri = seedRedirectUri();
        GrantTypeEntity grantType = seedGrantType();

        OauthClientEntity saved = repository.save(oauthClientEntity().withId(null)
                .withScopes(List.of(scope))
                .withRedirectUris(List.of(redirectUri))
                .withGrantTypes(List.of(grantType)));

        OauthClientDtoIn updateDto = otherOauthClientDtoIn()
                .withScopeIds(List.of(scope.getId()))
                .withRedirectUriIds(List.of(redirectUri.getId()))
                .withGrantTypeIds(List.of(grantType.getId()));

        OauthClientDtoOut response = webTestClient
                .put()
                .uri(PATH + "/" + saved.getId())
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateDto)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(OauthClientDtoOut.class)
                .returnResult()
                .getResponseBody();

        assertThat(response)
                .usingRecursiveComparison()
                .ignoringFields("scopes", "redirectUris", "grantTypes")
                .isEqualTo(otherOauthClientDtoOut(saved.getId()));
    }

    @Test
    void delete() {
        ScopeEntity scope = seedScope();
        RedirectUriEntity redirectUri = seedRedirectUri();
        GrantTypeEntity grantType = seedGrantType();

        OauthClientEntity saved = repository.save(oauthClientEntity().withId(null)
                .withScopes(List.of(scope))
                .withRedirectUris(List.of(redirectUri))
                .withGrantTypes(List.of(grantType)));

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

    private ScopeEntity seedScope() {
        return scopeRepository.save(readScopeEntity().withId(null));
    }

    private RedirectUriEntity seedRedirectUri() {
        return redirectUriRepository.save(redirectUriEntity().withId(null));
    }

    private GrantTypeEntity seedGrantType() {
        return grantTypeRepository.save(grantTypeEntity().withId(null));
    }
}
