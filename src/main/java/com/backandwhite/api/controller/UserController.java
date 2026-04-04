package com.backandwhite.api.controller;

import com.backandwhite.api.BaseApi;
import com.backandwhite.api.dto.OperationResponseDtoOut;
import com.backandwhite.api.dto.in.ForgotPasswordDtoIn;
import com.backandwhite.api.dto.in.ResetPasswordDtoIn;
import com.backandwhite.api.dto.in.ChangePasswordRequestDtoIn;
import com.backandwhite.api.dto.in.ConfirmPasswordChangeDtoIn;
import com.backandwhite.api.dto.in.RevokeSessionRequestDtoIn;
import com.backandwhite.api.dto.in.ConfirmRevokeSessionDtoIn;
import com.backandwhite.api.dto.in.UserDtoIn;
import com.backandwhite.api.dto.out.UserDtoOut;
import com.backandwhite.api.dto.out.UserSessionDtoOut;
import com.backandwhite.api.mapper.UserDtoMapper;
import com.backandwhite.api.validation.CreateValidation;
import com.backandwhite.api.validation.UpdateValidation;
import com.backandwhite.application.usecase.UserUseCase;
import com.backandwhite.common.exception.ArgumentException;
import com.backandwhite.domain.model.UserSession;
import com.backandwhite.domain.model.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User Operations.", description = "Operations related to users.")
public class UserController implements BaseApi<UserDtoIn, UserDtoOut, Long> {

    private final UserDtoMapper mapper;
    private final UserUseCase useCase;

    @PostMapping
    public ResponseEntity<UserDtoOut> create(
            @Validated({ Default.class, CreateValidation.class }) @RequestBody UserDtoIn dto,
            @RequestHeader(value = "Accept-Language", defaultValue = "es") String lang) {
        User entity = useCase.save(mapper.toDomain(dto), lang);
        return new ResponseEntity<>(mapper.toDtoOut(entity), HttpStatus.CREATED);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDtoOut> register(
            @Validated({ Default.class, CreateValidation.class }) @RequestBody UserDtoIn dto) {
        User entity = useCase.save(mapper.toDomain(dto));
        return new ResponseEntity<>(mapper.toDtoOut(entity), HttpStatus.CREATED);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<UserDtoOut> update(
            @Validated({ Default.class, UpdateValidation.class }) @RequestBody UserDtoIn dto, @PathVariable Long id) {
        User entity = useCase.update(mapper.toDomain(dto), id);
        return new ResponseEntity<>(mapper.toDtoOut(entity), HttpStatus.OK);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        useCase.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    @DeleteMapping("/batch")
    public ResponseEntity<Void> deleteAll(@RequestBody List<Long> ids) {
        useCase.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserDtoOut> getById(@PathVariable Long id) {
        return new ResponseEntity<>(mapper.toDtoOut(useCase.getById(id)), HttpStatus.OK);
    }

    @Override
    @GetMapping
    public ResponseEntity<List<UserDtoOut>> findAll() {
        return new ResponseEntity<>(mapper.toDtoOutList(useCase.findAll()), HttpStatus.OK);
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<UserDtoOut> toggleEnabled(@PathVariable Long id) {
        User entity = useCase.toggleEnabled(id);
        return new ResponseEntity<>(mapper.toDtoOut(entity), HttpStatus.OK);
    }

    @GetMapping("/activate")
    public ResponseEntity<Void> activateUser(
            @RequestParam String token,
            @RequestParam(defaultValue = "es") String lang) {
        try {
            useCase.activateUser(token, lang);
            log.info("::> User activated successfully with token");
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/activation-success.html"))
                    .build();
        } catch (ArgumentException ex) {
            log.warn("::> Activation failed: {}", ex.getMessage());
            String encodedMsg = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/activation-error.html?message=" + encodedMsg))
                    .build();
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<OperationResponseDtoOut> forgotPassword(
            @Valid @RequestBody ForgotPasswordDtoIn dto,
            @RequestHeader(value = "Accept-Language", defaultValue = "es") String lang) {
        useCase.requestPasswordReset(dto.getEmail(), lang);
        // Always return success to not reveal if the email exists
        return ResponseEntity.ok(
                OperationResponseDtoOut.builder()
                        .code("OK")
                        .message("Si el correo está registrado, recibirás un enlace para restablecer tu contraseña.")
                        .details(List.of())
                        .dateTime(ZonedDateTime.now())
                        .build());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordDtoIn dto) {
        try {
            useCase.resetPassword(dto.getToken().trim(), dto.getNewPassword());
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/reset-success.html"))
                    .build();
        } catch (ArgumentException ex) {
            log.warn("::> Password reset failed: {}", ex.getMessage());
            String encodedMsg = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/reset-error.html?message=" + encodedMsg))
                    .build();
        }
    }

    @PostMapping("/change-password/request")
    public ResponseEntity<OperationResponseDtoOut> requestPasswordChange(
            @Valid @RequestBody ChangePasswordRequestDtoIn dto,
            @RequestHeader(value = "X-Auth-Email") String email) {
        useCase.requestPasswordChange(
                email.trim().toLowerCase(),
                dto.getCurrentPassword(),
                dto.getNewPassword(),
                dto.getConfirmPassword());
        return ResponseEntity.ok(
                OperationResponseDtoOut.builder()
                        .code("OK")
                        .message("Se ha enviado un código de verificación a tu correo electrónico.")
                        .details(List.of())
                        .dateTime(ZonedDateTime.now())
                        .build());
    }

    @PostMapping("/change-password/confirm")
    public ResponseEntity<OperationResponseDtoOut> confirmPasswordChange(
            @Valid @RequestBody ConfirmPasswordChangeDtoIn dto,
            @RequestHeader(value = "X-Auth-Email") String email) {
        useCase.confirmPasswordChange(email.trim().toLowerCase(), dto.getCode());
        return ResponseEntity.ok(
                OperationResponseDtoOut.builder()
                        .code("OK")
                        .message("Tu contraseña ha sido actualizada correctamente.")
                        .details(List.of())
                        .dateTime(ZonedDateTime.now())
                        .build());
    }

    // ─── Session management ─────────────────────────────────────────

    @GetMapping("/sessions")
    public ResponseEntity<List<UserSessionDtoOut>> getActiveSessions(
            @RequestHeader(value = "X-Auth-Email") String email) {
        List<UserSession> sessions = useCase.getActiveSessions(email.trim().toLowerCase());
        List<UserSessionDtoOut> dtos = sessions.stream()
                .map(s -> UserSessionDtoOut.builder()
                        .sessionId(s.getSessionId())
                        .deviceInfo(s.getDeviceInfo())
                        .ipAddress(s.getIpAddress())
                        .createdAt(s.getCreatedAt())
                        .lastActiveAt(s.getLastActiveAt())
                        .build())
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/sessions/revoke/request")
    public ResponseEntity<OperationResponseDtoOut> requestSessionRevoke(
            @Valid @RequestBody RevokeSessionRequestDtoIn dto,
            @RequestHeader(value = "X-Auth-Email") String email) {
        useCase.requestSessionRevoke(email.trim().toLowerCase(), dto.getSessionId());
        return ResponseEntity.ok(
                OperationResponseDtoOut.builder()
                        .code("OK")
                        .message("Se ha enviado un código de verificación a tu correo electrónico.")
                        .details(List.of())
                        .dateTime(ZonedDateTime.now())
                        .build());
    }

    @PostMapping("/sessions/revoke/confirm")
    public ResponseEntity<OperationResponseDtoOut> confirmSessionRevoke(
            @Valid @RequestBody ConfirmRevokeSessionDtoIn dto,
            @RequestHeader(value = "X-Auth-Email") String email) {
        useCase.confirmSessionRevoke(email.trim().toLowerCase(), dto.getCode());
        return ResponseEntity.ok(
                OperationResponseDtoOut.builder()
                        .code("OK")
                        .message("La sesión ha sido cerrada correctamente.")
                        .details(List.of())
                        .dateTime(ZonedDateTime.now())
                        .build());
    }
}
