package com.backandwhite.integration;

import com.backandwhite.api.dto.in.UserDtoIn;
import com.backandwhite.api.dto.out.UserDtoOut;
import com.backandwhite.config.BaseIntegration;
import com.backandwhite.infrastructure.db.postgres.entity.GroupEntity;
import com.backandwhite.infrastructure.db.postgres.entity.RoleEntity;
import com.backandwhite.infrastructure.db.postgres.entity.ScopeEntity;
import com.backandwhite.infrastructure.db.postgres.entity.UserEntity;
import com.backandwhite.infrastructure.db.postgres.repository.GroupJpaRepositoryAdapter;
import com.backandwhite.infrastructure.db.postgres.repository.RoleJpaRepositoryAdapter;
import com.backandwhite.infrastructure.db.postgres.repository.ScopeJpaRepositoryAdapter;
import com.backandwhite.infrastructure.db.postgres.repository.UserJpaRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

import static com.backandwhite.provider.GroupProvider.adminGroupEntity;
import static com.backandwhite.provider.GroupProvider.userGroupEntity;
import static com.backandwhite.provider.RoleProvider.*;
import static com.backandwhite.provider.ScopeProvider.readScopeEntity;
import static com.backandwhite.provider.ScopeProvider.writeScopeEntity;
import static com.backandwhite.provider.UserProvider.*;
import static org.assertj.core.api.Assertions.assertThat;

class UserControllerIT extends BaseIntegration {

        private static final String PATH = "/api/v1/users";

        @Autowired
        private UserJpaRepositoryAdapter repository;

        @Autowired
        private ScopeJpaRepositoryAdapter scopeRepository;

        @Autowired
        private RoleJpaRepositoryAdapter roleRepository;

        @Autowired
        private GroupJpaRepositoryAdapter groupRepository;

        @Test
        void create() {
                ScopeEntity scope = scopeRepository.save(readScopeEntity().withId(null));
                RoleEntity role = roleRepository.save(adminRoleEntity().withId(null));
                GroupEntity group = groupRepository.save(adminGroupEntity().withId(null)
                                .withRoles(List.of(role)));

                UserDtoIn dtoIn = userDtoIn()
                                .withScopeIds(List.of(scope.getId()))
                                .withRoleIds(List.of(role.getId()))
                                .withGroupIds(List.of(group.getId()));

                UserDtoOut response = webTestClient
                                .post()
                                .uri(PATH)
                                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(dtoIn)
                                .exchange()
                                .expectStatus().isCreated()
                                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                .expectBody(UserDtoOut.class)
                                .returnResult()
                                .getResponseBody();

                assertThat(response)
                                .usingRecursiveComparison()
                                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy",
                                                ".*updatedBy")
                                .ignoringFields("id", "password", "scopes", "roles", "groups")
                                .isEqualTo(userDtoOut(null));
        }

        @Test
        void findAll() {
                ScopeEntity scope = scopeRepository.save(readScopeEntity().withId(null));
                ScopeEntity otherScope = scopeRepository.save(writeScopeEntity().withId(null));
                RoleEntity role = roleRepository.save(adminRoleEntity().withId(null));
                RoleEntity otherRole = roleRepository.save(userRoleEntity().withId(null));
                GroupEntity group = groupRepository.save(adminGroupEntity().withId(null)
                                .withRoles(List.of(role)));
                GroupEntity otherGroup = groupRepository.save(userGroupEntity().withId(null)
                                .withRoles(List.of(otherRole)));

                repository.saveAll(List.of(
                                userEntity().withId(null)
                                                .withScopes(List.of(scope))
                                                .withRoles(List.of(role))
                                                .withGroups(List.of(group)),
                                otherUserEntity().withId(null)
                                                .withScopes(List.of(otherScope))
                                                .withRoles(List.of(otherRole))
                                                .withGroups(List.of(otherGroup))));

                List<UserDtoOut> response = webTestClient
                                .get()
                                .uri(PATH)
                                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                .expectBodyList(UserDtoOut.class)
                                .returnResult()
                                .getResponseBody();

                assertThat(response)
                                .usingRecursiveComparison()
                                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy",
                                                ".*updatedBy")
                                .ignoringFields("id", "password", "scopes", "roles", "groups")
                                .isEqualTo(List.of(
                                                userDtoOut(null),
                                                otherUserDtoOut(null)));
        }

        @Test
        void getById() {
                ScopeEntity scope = scopeRepository.save(readScopeEntity().withId(null));
                RoleEntity role = roleRepository.save(adminRoleEntity().withId(null));
                GroupEntity group = groupRepository.save(adminGroupEntity().withId(null)
                                .withRoles(List.of(role)));

                UserEntity saved = repository.save(userEntity().withId(null)
                                .withScopes(List.of(scope))
                                .withRoles(List.of(role))
                                .withGroups(List.of(group)));

                UserDtoOut response = webTestClient
                                .get()
                                .uri(PATH + "/" + saved.getId())
                                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                .expectBody(UserDtoOut.class)
                                .returnResult()
                                .getResponseBody();

                assertThat(response)
                                .usingRecursiveComparison()
                                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy",
                                                ".*updatedBy")
                                .ignoringFields("password", "scopes", "roles", "groups")
                                .isEqualTo(userDtoOut(saved.getId()));
        }

        @Test
        void update() {
                ScopeEntity scope = scopeRepository.save(readScopeEntity().withId(null));
                RoleEntity role = roleRepository.save(adminRoleEntity().withId(null));
                GroupEntity group = groupRepository.save(adminGroupEntity().withId(null)
                                .withRoles(List.of(role)));
                ScopeEntity otherScope = scopeRepository.save(writeScopeEntity().withId(null));
                RoleEntity otherRole = roleRepository.save(userRoleEntity().withId(null));
                GroupEntity otherGroup = groupRepository.save(userGroupEntity().withId(null)
                                .withRoles(List.of(otherRole)));

                UserEntity saved = repository.save(userEntity().withId(null)
                                .withScopes(List.of(scope))
                                .withRoles(List.of(role))
                                .withGroups(List.of(group)));
                UserDtoIn updateDto = otherUserDtoIn()
                                .withScopeIds(List.of(otherScope.getId()))
                                .withRoleIds(List.of(otherRole.getId()))
                                .withGroupIds(List.of(otherGroup.getId()));

                UserDtoOut response = webTestClient
                                .put()
                                .uri(PATH + "/" + saved.getId())
                                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(updateDto)
                                .exchange()
                                .expectStatus().isOk()
                                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                                .expectBody(UserDtoOut.class)
                                .returnResult()
                                .getResponseBody();

                assertThat(response)
                                .usingRecursiveComparison()
                                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy",
                                                ".*updatedBy")
                                .ignoringFields("password", "scopes", "roles", "groups")
                                .isEqualTo(otherUserDtoOut(saved.getId()));
        }

        @Test
        void delete() {
                ScopeEntity scope = scopeRepository.save(readScopeEntity().withId(null));
                RoleEntity role = roleRepository.save(adminRoleEntity().withId(null));
                GroupEntity group = groupRepository.save(adminGroupEntity().withId(null)
                                .withRoles(List.of(role)));

                UserEntity saved = repository.save(userEntity().withId(null)
                                .withScopes(List.of(scope))
                                .withRoles(List.of(role))
                                .withGroups(List.of(group)));

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
