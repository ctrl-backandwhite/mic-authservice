package com.backandwhite.api.controller;

import static com.backandwhite.common.exception.Message.VALIDATION_ERROR;
import static com.backandwhite.provider.UserProvider.OTHER_USER_ID;
import static com.backandwhite.provider.UserProvider.USER_EMAIL;
import static com.backandwhite.provider.UserProvider.USER_ID;
import static com.backandwhite.provider.UserProvider.user;
import static com.backandwhite.provider.UserProvider.userDtoIn;
import static com.backandwhite.provider.UserProvider.userDtoOut;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.backandwhite.api.dto.OperationResponseDtoOut;
import com.backandwhite.api.dto.in.ChangePasswordRequestDtoIn;
import com.backandwhite.api.dto.in.ConfirmPasswordChangeDtoIn;
import com.backandwhite.api.dto.in.ConfirmRevokeSessionDtoIn;
import com.backandwhite.api.dto.in.ForgotPasswordDtoIn;
import com.backandwhite.api.dto.in.ResetPasswordDtoIn;
import com.backandwhite.api.dto.in.RevokeSessionRequestDtoIn;
import com.backandwhite.api.dto.in.UserDtoIn;
import com.backandwhite.api.dto.out.UserDtoOut;
import com.backandwhite.api.mapper.UserDtoMapper;
import com.backandwhite.api.mapper.UserSessionDtoMapper;
import com.backandwhite.application.usecase.UserUseCase;
import com.backandwhite.domain.model.User;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserDtoMapper mapper;

    @Mock
    private UserSessionDtoMapper sessionMapper;

    @Mock
    private UserUseCase useCase;

    @InjectMocks
    private UserController controller;

    @Test
    void create_returnsCreatedDto() {
        UserDtoIn dtoIn = userDtoIn();
        User model = user();
        UserDtoOut dtoOut = userDtoOut(USER_ID);

        when(mapper.toDomain(dtoIn)).thenReturn(model);
        when(useCase.save(model, "es")).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<UserDtoOut> response = controller.create(dtoIn, "es");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(mapper).toDomain(dtoIn);
        verify(useCase).save(model, "es");
        verify(mapper).toDtoOut(model);
    }

    @Test
    void update_returnsUpdatedDto() {
        UserDtoIn dtoIn = userDtoIn();
        User model = user();
        UserDtoOut dtoOut = userDtoOut(USER_ID);

        when(mapper.toDomain(dtoIn)).thenReturn(model);
        when(useCase.update(model, USER_ID)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<UserDtoOut> response = controller.update(dtoIn, USER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(mapper).toDomain(dtoIn);
        verify(useCase).update(model, USER_ID);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void delete_returnsNoContent() {
        ResponseEntity<Void> response = controller.delete(USER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(useCase).delete(USER_ID);
    }

    @Test
    void getById_returnsDto() {
        User model = user();
        UserDtoOut dtoOut = userDtoOut(USER_ID);

        when(useCase.getById(USER_ID)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<UserDtoOut> response = controller.getById(USER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(useCase).getById(USER_ID);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void findAll_returnsDtoList() {
        List<User> models = List.of(user());
        List<UserDtoOut> dtoOuts = List.of(userDtoOut(USER_ID));

        when(useCase.findAll()).thenReturn(models);
        when(mapper.toDtoOutList(models)).thenReturn(dtoOuts);

        ResponseEntity<List<UserDtoOut>> response = controller.findAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOuts);
        verify(useCase).findAll();
        verify(mapper).toDtoOutList(models);
    }

    @Test
    void toggleEnabled_returnsUpdatedDto() {
        User model = user();
        UserDtoOut dtoOut = userDtoOut(USER_ID);

        when(useCase.toggleEnabled(USER_ID)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<UserDtoOut> response = controller.toggleEnabled(USER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(dtoOut);
        verify(useCase).toggleEnabled(USER_ID);
        verify(mapper).toDtoOut(model);
    }

    @Test
    void deleteAll_returnsNoContent() {
        List<Long> ids = List.of(USER_ID, OTHER_USER_ID);

        ResponseEntity<Void> response = controller.deleteAll(ids);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(useCase).deleteAll(ids);
    }

    @Test
    void forgotPassword_returnsOk() {
        ForgotPasswordDtoIn dto = ForgotPasswordDtoIn.builder().email(USER_EMAIL).build();
        doNothing().when(useCase).requestPasswordReset(USER_EMAIL, "es");

        ResponseEntity<OperationResponseDtoOut> response = controller.forgotPassword(dto, "es");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo("OK");
        assertThat(response.getBody().getMessage()).contains("If the email");
        verify(useCase).requestPasswordReset(USER_EMAIL, "es");
    }

    @Test
    void resetPassword_redirectsToSuccess() {
        ResetPasswordDtoIn dto = ResetPasswordDtoIn.builder().token("valid-token").newPassword("NewPass1").build();
        doNothing().when(useCase).resetPassword("valid-token", "NewPass1");

        ResponseEntity<Void> response = controller.resetPassword(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/reset-success.html");
        verify(useCase).resetPassword("valid-token", "NewPass1");
    }

    @Test
    void resetPassword_whenArgumentException_redirectsToError() {
        ResetPasswordDtoIn dto = ResetPasswordDtoIn.builder().token("bad-token").newPassword("NewPass1").build();
        doThrow(VALIDATION_ERROR.toArgumentException("El enlace de recuperación es inválido o ya fue utilizado."))
                .when(useCase).resetPassword("bad-token", "NewPass1");

        ResponseEntity<Void> response = controller.resetPassword(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getHeaders().getLocation().toString()).contains("/reset-error.html");
        verify(useCase).resetPassword("bad-token", "NewPass1");
    }

    // ─── register ──────────────────────────────────────────

    @Test
    void register_returnsCreatedDto() {
        UserDtoIn dtoIn = userDtoIn();
        User model = user();
        UserDtoOut dtoOut = userDtoOut(USER_ID);

        when(mapper.toDomain(dtoIn)).thenReturn(model);
        when(useCase.save(model)).thenReturn(model);
        when(mapper.toDtoOut(model)).thenReturn(dtoOut);

        ResponseEntity<UserDtoOut> response = controller.register(dtoIn);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(dtoOut);
    }

    // ─── activateUser ──────────────────────────────────────

    @Test
    void activateUser_success_redirectsToSuccessPage() {
        doNothing().when(useCase).activateUser("valid-token", "es");

        ResponseEntity<Void> response = controller.activateUser("valid-token", "es");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/activation-success.html");
    }

    @Test
    void activateUser_invalidToken_redirectsToErrorPage() {
        doThrow(VALIDATION_ERROR.toArgumentException("Invalid activation token."))
                .when(useCase).activateUser("bad-token", "es");

        ResponseEntity<Void> response = controller.activateUser("bad-token", "es");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().toString()).contains("/activation-error.html");
    }

    // ─── requestPasswordChange / confirmPasswordChange ─────

    @Test
    void requestPasswordChange_returnsOk() {
        ChangePasswordRequestDtoIn dto = ChangePasswordRequestDtoIn.builder()
                .currentPassword("old").newPassword("new").confirmPassword("new").build();
        doNothing().when(useCase).requestPasswordChange(USER_EMAIL, "old", "new", "new");

        ResponseEntity<OperationResponseDtoOut> response = controller.requestPasswordChange(dto, USER_EMAIL);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("OK");
        verify(useCase).requestPasswordChange(USER_EMAIL, "old", "new", "new");
    }

    @Test
    void confirmPasswordChange_returnsOk() {
        ConfirmPasswordChangeDtoIn dto = ConfirmPasswordChangeDtoIn.builder().code("123456").build();
        doNothing().when(useCase).confirmPasswordChange(USER_EMAIL, "123456");

        ResponseEntity<OperationResponseDtoOut> response = controller.confirmPasswordChange(dto, USER_EMAIL);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("OK");
    }

    // ─── session management ────────────────────────────────

    @Test
    void getActiveSessions_returnsSessionList() {
        var session = com.backandwhite.domain.model.UserSession.builder()
                .sessionId("sess-1").deviceInfo("Chrome · Windows")
                .ipAddress("1.2.3.4").createdAt(java.time.Instant.now())
                .lastActiveAt(java.time.Instant.now()).build();
        var sessionDto = com.backandwhite.api.dto.out.UserSessionDtoOut.builder()
                .sessionId("sess-1").deviceInfo("Chrome · Windows").build();
        when(useCase.getActiveSessions(USER_EMAIL)).thenReturn(java.util.List.of(session));
        when(sessionMapper.toDtoOutList(java.util.List.of(session))).thenReturn(java.util.List.of(sessionDto));

        ResponseEntity<java.util.List<com.backandwhite.api.dto.out.UserSessionDtoOut>> response =
                controller.getActiveSessions(USER_EMAIL);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getSessionId()).isEqualTo("sess-1");
    }

    @Test
    void requestSessionRevoke_returnsOk() {
        RevokeSessionRequestDtoIn dto = RevokeSessionRequestDtoIn.builder().sessionId("sess-1").build();
        doNothing().when(useCase).requestSessionRevoke(USER_EMAIL, "sess-1");

        ResponseEntity<OperationResponseDtoOut> response = controller.requestSessionRevoke(dto, USER_EMAIL);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("OK");
    }

    @Test
    void confirmSessionRevoke_returnsOk() {
        ConfirmRevokeSessionDtoIn dto = ConfirmRevokeSessionDtoIn.builder().code("654321").build();
        doNothing().when(useCase).confirmSessionRevoke(USER_EMAIL, "654321");

        ResponseEntity<OperationResponseDtoOut> response = controller.confirmSessionRevoke(dto, USER_EMAIL);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("OK");
    }
}
