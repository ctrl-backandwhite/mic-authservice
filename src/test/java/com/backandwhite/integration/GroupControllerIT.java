package com.backandwhite.integration;

import static com.backandwhite.provider.GroupProvider.*;
import static com.backandwhite.provider.RoleProvider.*;
import static com.backandwhite.provider.RoleProvider.ADMIN_UNIQUE_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.api.dto.in.GroupDtoIn;
import com.backandwhite.api.dto.out.GroupDtoOut;
import com.backandwhite.config.BaseIntegration;
import com.backandwhite.infrastructure.db.postgres.entity.GroupEntity;
import com.backandwhite.infrastructure.db.postgres.entity.RoleEntity;
import com.backandwhite.infrastructure.db.postgres.repository.GroupJpaRepositoryAdapter;
import com.backandwhite.infrastructure.db.postgres.repository.RoleJpaRepositoryAdapter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

class GroupControllerIT extends BaseIntegration {

    private static final String PATH = "/api/v1/groups";

    @Autowired
    private GroupJpaRepositoryAdapter repository;

    @Autowired
    private RoleJpaRepositoryAdapter roleRepository;

    @Test
    void create() {
        RoleEntity role = roleRepository.save(adminRoleEntity().withId(null));
        GroupDtoIn dtoIn = adminGroupDtoIn().withRoleIds(List.of(role.getId()));

        GroupDtoOut response = webTestClient.post().uri(PATH)
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                .contentType(MediaType.APPLICATION_JSON).bodyValue(dtoIn).exchange().expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody(GroupDtoOut.class).returnResult()
                .getResponseBody();

        assertThat(response).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy", ".*updatedBy")
                .ignoringFields("id", "roles").isEqualTo(adminGroupDtoOut(null));
    }

    @Test
    void findAll() {
        RoleEntity adminRole = roleRepository.save(adminRoleEntity().withId(null));
        RoleEntity userRole = roleRepository.save(userRoleEntity().withId(null));
        repository.saveAll(List.of(adminGroupEntity().withId(null).withRoles(List.of(adminRole)),
                userGroupEntity().withId(null).withRoles(List.of(userRole))));

        List<GroupDtoOut> response = webTestClient.get().uri(PATH)
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME))).exchange().expectStatus()
                .isOk().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBodyList(GroupDtoOut.class)
                .returnResult().getResponseBody();

        assertThat(response).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy", ".*updatedBy")
                .ignoringFields("id", "roles").isEqualTo(List.of(adminGroupDtoOut(null), userGroupDtoOut(null)));
    }

    @Test
    void getById() {
        RoleEntity adminRole = roleRepository.save(adminRoleEntity().withId(null));
        GroupEntity saved = repository.save(adminGroupEntity().withId(null).withRoles(List.of(adminRole)));

        GroupDtoOut response = webTestClient.get().uri(PATH + "/" + saved.getId())
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME))).exchange().expectStatus()
                .isOk().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody(GroupDtoOut.class)
                .returnResult().getResponseBody();

        assertThat(response).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy", ".*updatedBy")
                .ignoringFields("roles").isEqualTo(adminGroupDtoOut(saved.getId()));
    }

    @Test
    void update() {
        RoleEntity adminRole = roleRepository.save(adminRoleEntity().withId(null));
        RoleEntity userRole = roleRepository.save(userRoleEntity().withId(null));
        GroupEntity saved = repository.save(adminGroupEntity().withId(null).withRoles(List.of(adminRole)));
        GroupDtoIn updateDto = userGroupDtoIn().withRoleIds(List.of(userRole.getId()));

        GroupDtoOut response = webTestClient.put().uri(PATH + "/" + saved.getId())
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                .contentType(MediaType.APPLICATION_JSON).bodyValue(updateDto).exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody(GroupDtoOut.class).returnResult()
                .getResponseBody();

        assertThat(response).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy", ".*updatedBy")
                .ignoringFields("roles").isEqualTo(userGroupDtoOut(saved.getId()));
    }

    @Test
    void delete() {
        RoleEntity adminRole = roleRepository.save(adminRoleEntity().withId(null));
        GroupEntity saved = repository.save(adminGroupEntity().withId(null).withRoles(List.of(adminRole)));

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
