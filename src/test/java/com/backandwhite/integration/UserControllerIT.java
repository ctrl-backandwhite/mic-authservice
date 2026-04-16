package com.backandwhite.integration;

import static com.backandwhite.provider.GroupProvider.adminGroupEntity;
import static com.backandwhite.provider.GroupProvider.userGroupEntity;
import static com.backandwhite.provider.RoleProvider.*;
import static com.backandwhite.provider.UserProvider.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.backandwhite.api.dto.OperationResponseDtoOut;
import com.backandwhite.api.dto.in.ChangePasswordRequestDtoIn;
import com.backandwhite.api.dto.in.ForgotPasswordDtoIn;
import com.backandwhite.api.dto.in.ResetPasswordDtoIn;
import com.backandwhite.api.dto.in.UserDtoIn;
import com.backandwhite.api.dto.out.UserDtoOut;
import com.backandwhite.config.BaseIntegration;
import com.backandwhite.infrastructure.db.postgres.entity.GroupEntity;
import com.backandwhite.infrastructure.db.postgres.entity.RoleEntity;
import com.backandwhite.infrastructure.db.postgres.entity.UserEntity;
import com.backandwhite.infrastructure.db.postgres.repository.GroupJpaRepositoryAdapter;
import com.backandwhite.infrastructure.db.postgres.repository.RoleJpaRepositoryAdapter;
import com.backandwhite.infrastructure.db.postgres.repository.UserJpaRepositoryAdapter;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserControllerIT extends BaseIntegration {

    private static final String PATH = "/api/v1/users";

    @Autowired
    private UserJpaRepositoryAdapter repository;

    @Autowired
    private RoleJpaRepositoryAdapter roleRepository;

    @Autowired
    private GroupJpaRepositoryAdapter groupRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void create() {
        RoleEntity role = roleRepository.save(adminRoleEntity().withId(null));
        GroupEntity group = groupRepository.save(adminGroupEntity().withId(null).withRoles(List.of(role)));

        UserDtoIn dtoIn = userDtoIn().withRoleIds(List.of(role.getId())).withGroupIds(List.of(group.getId()));

        UserDtoOut response = webTestClient.post().uri(PATH)
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                .contentType(MediaType.APPLICATION_JSON).bodyValue(dtoIn).exchange().expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody(UserDtoOut.class).returnResult()
                .getResponseBody();

        UserDtoOut expected = userDtoOut(null);
        expected.setEnabled(false);

        assertThat(response).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy", ".*updatedBy")
                .ignoringFields("id", "password", "roles", "groups").isEqualTo(expected);
    }

    @Test
    void findAll() {
        RoleEntity role = roleRepository.save(adminRoleEntity().withId(null));
        RoleEntity otherRole = roleRepository.save(userRoleEntity().withId(null));
        GroupEntity group = groupRepository.save(adminGroupEntity().withId(null).withRoles(List.of(role)));
        GroupEntity otherGroup = groupRepository.save(userGroupEntity().withId(null).withRoles(List.of(otherRole)));

        repository.saveAll(List.of(userEntity().withId(null).withRoles(List.of(role)).withGroups(List.of(group)),
                otherUserEntity().withId(null).withRoles(List.of(otherRole)).withGroups(List.of(otherGroup))));

        List<UserDtoOut> response = webTestClient.get().uri(PATH)
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME))).exchange().expectStatus()
                .isOk().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBodyList(UserDtoOut.class)
                .returnResult().getResponseBody();

        assertThat(response).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy", ".*updatedBy")
                .ignoringFields("id", "password", "roles", "groups")
                .isEqualTo(List.of(userDtoOut(null), otherUserDtoOut(null)));
    }

    @Test
    void getById() {
        RoleEntity role = roleRepository.save(adminRoleEntity().withId(null));
        GroupEntity group = groupRepository.save(adminGroupEntity().withId(null).withRoles(List.of(role)));

        UserEntity saved = repository
                .save(userEntity().withId(null).withRoles(List.of(role)).withGroups(List.of(group)));

        UserDtoOut response = webTestClient.get().uri(PATH + "/" + saved.getId())
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME))).exchange().expectStatus()
                .isOk().expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody(UserDtoOut.class)
                .returnResult().getResponseBody();

        assertThat(response).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy", ".*updatedBy")
                .ignoringFields("password", "roles", "groups").isEqualTo(userDtoOut(saved.getId()));
    }

    @Test
    void update() {
        RoleEntity role = roleRepository.save(adminRoleEntity().withId(null));
        GroupEntity group = groupRepository.save(adminGroupEntity().withId(null).withRoles(List.of(role)));
        RoleEntity otherRole = roleRepository.save(userRoleEntity().withId(null));
        GroupEntity otherGroup = groupRepository.save(userGroupEntity().withId(null).withRoles(List.of(otherRole)));

        UserEntity saved = repository
                .save(userEntity().withId(null).withRoles(List.of(role)).withGroups(List.of(group)));
        UserDtoIn updateDto = otherUserDtoIn().withRoleIds(List.of(otherRole.getId()))
                .withGroupIds(List.of(otherGroup.getId()));

        UserDtoOut response = webTestClient.put().uri(PATH + "/" + saved.getId())
                .header(HttpHeaders.AUTHORIZATION, getToken(List.of(ADMIN_UNIQUE_NAME)))
                .contentType(MediaType.APPLICATION_JSON).bodyValue(updateDto).exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON).expectBody(UserDtoOut.class).returnResult()
                .getResponseBody();

        assertThat(response).usingRecursiveComparison()
                .ignoringFieldsMatchingRegexes(".*createdAt", ".*updatedAt", ".*createdBy", ".*updatedBy")
                .ignoringFields("password", "roles", "groups").isEqualTo(otherUserDtoOut(saved.getId()));
    }

    @Test
    void delete() {
        RoleEntity role = roleRepository.save(adminRoleEntity().withId(null));
        GroupEntity group = groupRepository.save(adminGroupEntity().withId(null).withRoles(List.of(role)));

        UserEntity saved = repository
                .save(userEntity().withId(null).withRoles(List.of(role)).withGroups(List.of(group)));

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

    @Test
    void forgotPassword_returnsOk() {
        ForgotPasswordDtoIn dto = ForgotPasswordDtoIn.builder().email("nonexistent@example.com").build();

        OperationResponseDtoOut response = webTestClient.post().uri(PATH + "/forgot-password")
                .contentType(MediaType.APPLICATION_JSON).bodyValue(dto).exchange().expectStatus().isOk()
                .expectBody(OperationResponseDtoOut.class).returnResult().getResponseBody();

        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo("OK");
        assertThat(response.getMessage()).contains("If the email");
    }

    @Test
    void forgotPassword_whenEmailBlank_returnsBadRequest() {
        ForgotPasswordDtoIn dto = ForgotPasswordDtoIn.builder().email("").build();

        webTestClient.post().uri(PATH + "/forgot-password").contentType(MediaType.APPLICATION_JSON).bodyValue(dto)
                .exchange().expectStatus().isBadRequest();
    }

    @Test
    void forgotPassword_whenInvalidEmailFormat_returnsBadRequest() {
        ForgotPasswordDtoIn dto = ForgotPasswordDtoIn.builder().email("not-an-email").build();

        webTestClient.post().uri(PATH + "/forgot-password").contentType(MediaType.APPLICATION_JSON).bodyValue(dto)
                .exchange().expectStatus().isBadRequest();
    }

    @Test
    void resetPassword_whenInvalidToken_redirectsToError() {
        ResetPasswordDtoIn dto = ResetPasswordDtoIn.builder().token("non-existent-token").newPassword("NewPass1")
                .build();

        webTestClient.post().uri(PATH + "/reset-password").contentType(MediaType.APPLICATION_JSON).bodyValue(dto)
                .exchange().expectStatus().isFound().expectHeader()
                .value("Location", location -> assertThat(location).contains("/reset-error.html"));
    }

    @Test
    void resetPassword_whenValidToken_redirectsToSuccess() {
        String resetToken = "test-reset-token-abc123";
        repository.save(userEntity().withId(null).withNickName("reset.test").withEmail("reset.test@example.com")
                .withPasswordResetToken(resetToken)
                .withPasswordResetTokenExpiry(Instant.now().plus(30, ChronoUnit.MINUTES)).withRoles(List.of())
                .withGroups(List.of()));

        ResetPasswordDtoIn dto = ResetPasswordDtoIn.builder().token(resetToken).newPassword("NewPass1").build();

        webTestClient.post().uri(PATH + "/reset-password").contentType(MediaType.APPLICATION_JSON).bodyValue(dto)
                .exchange().expectStatus().isFound().expectHeader()
                .value("Location", location -> assertThat(location).endsWith("/reset-success.html"));
    }

    @Nested
    class Security {

        @Test
        void create_withUserRole_returnsForbidden() {
            UserDtoIn dtoIn = userDtoIn();
            webTestClient.post().uri(PATH).header(HttpHeaders.AUTHORIZATION, getToken(List.of(USER_UNIQUE_NAME)))
                    .contentType(MediaType.APPLICATION_JSON).bodyValue(dtoIn).exchange().expectStatus().isForbidden();
        }

        @Test
        void create_withoutToken_returnsUnauthorized() {
            webTestClient.post().uri(PATH).contentType(MediaType.APPLICATION_JSON).bodyValue(userDtoIn()).exchange()
                    .expectStatus().isUnauthorized();
        }

        @Test
        void findAll_withUserRole_returnsForbidden() {
            webTestClient.get().uri(PATH).header(HttpHeaders.AUTHORIZATION, getToken(List.of(USER_UNIQUE_NAME)))
                    .exchange().expectStatus().isForbidden();
        }

        @Test
        void findAll_withoutToken_returnsUnauthorized() {
            webTestClient.get().uri(PATH).exchange().expectStatus().isUnauthorized();
        }

        @Test
        void forgotPassword_withoutToken_isPermitted() {
            ForgotPasswordDtoIn dto = ForgotPasswordDtoIn.builder().email("anyone@example.com").build();
            webTestClient.post().uri(PATH + "/forgot-password").contentType(MediaType.APPLICATION_JSON).bodyValue(dto)
                    .exchange().expectStatus().isOk();
        }

        @Test
        void resetPassword_withoutToken_isPermitted() {
            ResetPasswordDtoIn dto = ResetPasswordDtoIn.builder().token("any-token").newPassword("NewPass1").build();
            webTestClient.post().uri(PATH + "/reset-password").contentType(MediaType.APPLICATION_JSON).bodyValue(dto)
                    .exchange().expectStatus().isFound();
        }

        @Test
        void changePasswordRequest_withUserRole_isPermitted() {
            repository.save(userEntity().withId(null).withNickName("cpuser").withEmail("cpuser@example.com")
                    .withPassword(passwordEncoder.encode("OldPass1")).withRoles(List.of()).withGroups(List.of()));

            ChangePasswordRequestDtoIn dto = ChangePasswordRequestDtoIn.builder().currentPassword("OldPass1")
                    .newPassword("NewPass1").confirmPassword("NewPass1").build();

            webTestClient.post().uri(PATH + "/change-password/request")
                    .header(HttpHeaders.AUTHORIZATION, getToken(List.of(USER_UNIQUE_NAME), "cpuser@example.com"))
                    .contentType(MediaType.APPLICATION_JSON).bodyValue(dto).exchange().expectStatus().isOk();
        }

        @Test
        void changePasswordRequest_withoutToken_returnsUnauthorized() {
            ChangePasswordRequestDtoIn dto = ChangePasswordRequestDtoIn.builder().currentPassword("OldPass1")
                    .newPassword("NewPass1").confirmPassword("NewPass1").build();

            webTestClient.post().uri(PATH + "/change-password/request").contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(dto).exchange().expectStatus().isUnauthorized();
        }

        @Test
        void sessions_withUserRole_isPermitted() {
            repository.save(userEntity().withId(null).withNickName("sessuser").withEmail("sessuser@example.com")
                    .withRoles(List.of()).withGroups(List.of()));

            webTestClient.get().uri(PATH + "/sessions")
                    .header(HttpHeaders.AUTHORIZATION, getToken(List.of(USER_UNIQUE_NAME), "sessuser@example.com"))
                    .exchange().expectStatus().isOk();
        }

        @Test
        void sessions_withoutToken_returnsUnauthorized() {
            webTestClient.get().uri(PATH + "/sessions").exchange().expectStatus().isUnauthorized();
        }
    }
}
