package com.backandwhite.api.controller;

import com.backandwhite.api.BaseApi;
import com.backandwhite.api.dto.OperationResponseDtoOut;
import com.backandwhite.api.dto.in.ChangePasswordRequestDtoIn;
import com.backandwhite.api.dto.in.ConfirmPasswordChangeDtoIn;
import com.backandwhite.api.dto.in.ConfirmRevokeSessionDtoIn;
import com.backandwhite.api.dto.in.ForgotPasswordDtoIn;
import com.backandwhite.api.dto.in.ResetPasswordDtoIn;
import com.backandwhite.api.dto.in.RevokeSessionRequestDtoIn;
import com.backandwhite.api.dto.in.UserDtoIn;
import com.backandwhite.api.dto.out.UserDtoOut;
import com.backandwhite.api.dto.out.UserSessionDtoOut;
import com.backandwhite.api.mapper.UserDtoMapper;
import com.backandwhite.api.mapper.UserSessionDtoMapper;
import com.backandwhite.api.validation.CreateValidation;
import com.backandwhite.api.validation.UpdateValidation;
import com.backandwhite.application.usecase.UserUseCase;
import com.backandwhite.common.exception.ArgumentException;
import com.backandwhite.common.security.annotation.NxAdmin;
import com.backandwhite.common.security.annotation.NxPublic;
import com.backandwhite.common.security.annotation.NxUser;
import com.backandwhite.domain.model.User;
import com.backandwhite.domain.model.UserSession;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Log4j2
@NxAdmin
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User Operations.", description = "Operations related to users.")
public class UserController implements BaseApi<UserDtoIn, UserDtoOut, Long> {

    private static final String CLAIM_EMAIL = "email";

    private final UserDtoMapper mapper;
    private final UserSessionDtoMapper sessionMapper;
    private final UserUseCase useCase;

    @PostMapping
    public ResponseEntity<UserDtoOut> create(
            @Validated({Default.class, CreateValidation.class}) @RequestBody UserDtoIn dto,
            @RequestHeader(value = "Accept-Language", defaultValue = "es") String lang) {
        User entity = useCase.save(mapper.toDomain(dto), lang);
        return new ResponseEntity<>(mapper.toDtoOut(entity), HttpStatus.CREATED);
    }

    @NxPublic
    @PostMapping("/register")
    public ResponseEntity<UserDtoOut> register(
            @Validated({Default.class, CreateValidation.class}) @RequestBody UserDtoIn dto) {
        User entity = useCase.save(mapper.toDomain(dto));
        return new ResponseEntity<>(mapper.toDtoOut(entity), HttpStatus.CREATED);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<UserDtoOut> update(
            @Validated({Default.class, UpdateValidation.class}) @RequestBody UserDtoIn dto, @PathVariable Long id) {
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

    @NxPublic
    @GetMapping("/activate")
    public ResponseEntity<Void> activateUser(@RequestParam String token,
            @RequestParam(defaultValue = "es") String lang) {
        try {
            useCase.activateUser(token, lang);
            log.info("::> User activated successfully with token");
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/activation-success.html")).build();
        } catch (ArgumentException ex) {
            log.warn("::> Activation failed: {}", ex.getMessage());
            String encodedMsg = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/activation-error.html?message=" + encodedMsg)).build();
        }
    }

    @NxPublic
    @PostMapping("/forgot-password")
    public ResponseEntity<OperationResponseDtoOut> forgotPassword(@Valid @RequestBody ForgotPasswordDtoIn dto,
            @RequestHeader(value = "Accept-Language", defaultValue = "es") String lang) {
        useCase.requestPasswordReset(dto.getEmail(), lang);
        // Always return success to not reveal if the email exists
        return ResponseEntity.ok(OperationResponseDtoOut
                .ok("If the email is registered, you will receive a link to reset your password."));
    }

    @NxPublic
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordDtoIn dto) {
        try {
            useCase.resetPassword(dto.getToken().trim(), dto.getNewPassword());
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/reset-success.html")).build();
        } catch (ArgumentException ex) {
            log.warn("::> Password reset failed: {}", ex.getMessage());
            String encodedMsg = URLEncoder.encode(ex.getMessage(), StandardCharsets.UTF_8);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/reset-error.html?message=" + encodedMsg)).build();
        }
    }

    @NxUser
    @PostMapping("/change-password/request")
    public ResponseEntity<OperationResponseDtoOut> requestPasswordChange(
            @Valid @RequestBody ChangePasswordRequestDtoIn dto, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString(CLAIM_EMAIL);
        useCase.requestPasswordChange(email.trim().toLowerCase(), dto.getCurrentPassword(), dto.getNewPassword(),
                dto.getConfirmPassword());
        return ResponseEntity
                .ok(OperationResponseDtoOut.ok("A verification code has been sent to your email address."));
    }

    @NxUser
    @PostMapping("/change-password/confirm")
    public ResponseEntity<OperationResponseDtoOut> confirmPasswordChange(
            @Valid @RequestBody ConfirmPasswordChangeDtoIn dto, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString(CLAIM_EMAIL);
        useCase.confirmPasswordChange(email.trim().toLowerCase(), dto.getCode());
        return ResponseEntity.ok(OperationResponseDtoOut.ok("Your password has been updated successfully."));
    }

    // ─── Session management ─────────────────────────────────────────

    @NxUser
    @GetMapping("/sessions")
    public ResponseEntity<List<UserSessionDtoOut>> getActiveSessions(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString(CLAIM_EMAIL);
        List<UserSession> sessions = useCase.getActiveSessions(email.trim().toLowerCase());
        return ResponseEntity.ok(sessionMapper.toDtoOutList(sessions));
    }

    @NxUser
    @PostMapping("/sessions/revoke/request")
    public ResponseEntity<OperationResponseDtoOut> requestSessionRevoke(
            @Valid @RequestBody RevokeSessionRequestDtoIn dto, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString(CLAIM_EMAIL);
        useCase.requestSessionRevoke(email.trim().toLowerCase(), dto.getSessionId());
        return ResponseEntity
                .ok(OperationResponseDtoOut.ok("A verification code has been sent to your email address."));
    }

    @NxUser
    @PostMapping("/sessions/revoke/confirm")
    public ResponseEntity<OperationResponseDtoOut> confirmSessionRevoke(
            @Valid @RequestBody ConfirmRevokeSessionDtoIn dto, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString(CLAIM_EMAIL);
        useCase.confirmSessionRevoke(email.trim().toLowerCase(), dto.getCode());
        return ResponseEntity.ok(OperationResponseDtoOut.ok("The session has been closed successfully."));
    }
}
