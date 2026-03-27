package com.backandwhite.api.controller;

import com.backandwhite.api.BaseApi;
import com.backandwhite.api.dto.in.UserDtoIn;
import com.backandwhite.api.dto.out.UserDtoOut;
import com.backandwhite.api.mapper.UserDtoMapper;
import com.backandwhite.api.validation.CreateValidation;
import com.backandwhite.api.validation.UpdateValidation;
import com.backandwhite.application.usecase.UserUseCase;
import com.backandwhite.common.exception.ArgumentException;
import com.backandwhite.domain.model.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import jakarta.validation.groups.Default;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Tag(name = "User Operations.", description = "Operations related to users.")
public class UserController implements BaseApi<UserDtoIn, UserDtoOut, Long> {

    private final UserDtoMapper mapper;
    private final UserUseCase useCase;

    @Override
    @PostMapping
    public ResponseEntity<UserDtoOut> create(
            @Validated({ Default.class, CreateValidation.class }) @RequestBody UserDtoIn dto) {
        User entity = useCase.save(mapper.toDomain(dto));
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
    public ResponseEntity<Void> activateUser(@RequestParam String token) {
        try {
            useCase.activateUser(token);
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
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "El correo es obligatorio."));
        }
        useCase.requestPasswordReset(email.trim());
        // Always return success to not reveal if the email exists
        return ResponseEntity.ok(Map.of("message",
                "Si el correo está registrado, recibirás un enlace para restablecer tu contraseña."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");
        if (token == null || token.isBlank() || newPassword == null || newPassword.isBlank()) {
            String msg = URLEncoder.encode("Datos incompletos.", StandardCharsets.UTF_8);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("/reset-error.html?message=" + msg))
                    .build();
        }
        try {
            useCase.resetPassword(token.trim(), newPassword);
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
}
