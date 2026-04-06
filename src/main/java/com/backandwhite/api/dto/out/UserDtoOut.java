package com.backandwhite.api.dto.out;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

@Data
@With
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoOut {

    @Schema(description = "Identificador único del usuario", example = "1", minimum = "1")
    private Long id;

    @Schema(description = "Nombre del usuario", example = "Juan", minLength = 2, maxLength = 100)
    private String name;

    @Schema(description = "Apellido del usuario", example = "Pérez García", minLength = 2, maxLength = 100)
    private String lastName;

    @Schema(description = "Nombre de usuario único para login", example = "juan.perez", minLength = 3, maxLength = 50)
    private String nickName;

    @Schema(description = "Dirección de correo electrónico del usuario", example = "juan.perez@ejemplo.com")
    private String email;

    @JsonIgnore
    @Schema(description = "Contraseña del usuario (no se retorna en respuestas por seguridad)", example = "***", hidden = true)
    private String password;

    @Schema(description = "Indica si la cuenta del usuario está habilitada", example = "true")
    private Boolean enabled;

    @Schema(description = "Indica si la cuenta del usuario no ha expirado", example = "true")
    private Boolean accountNonExpired;

    @Schema(description = "Indica si la cuenta del usuario no está bloqueada", example = "true")
    private Boolean accountNonLocked;

    @Schema(description = "Indica si las credenciales del usuario no han expirado", example = "true")
    private Boolean credentialsNonExpired;

    @JsonIgnoreProperties({ "createdAt", "updatedAt", "createdBy", "updatedBy" })
    @ArraySchema(schema = @Schema(implementation = RoleDtoOut.class), arraySchema = @Schema(description = "Roles directos asignados al usuario"))
    private List<RoleDtoOut> roles = new ArrayList<>();

    @JsonIgnoreProperties({ "createdAt", "updatedAt", "createdBy", "updatedBy" })
    @ArraySchema(schema = @Schema(implementation = GroupDtoOut.class), arraySchema = @Schema(description = "Grupos a los que pertenece el usuario"))
    private List<GroupDtoOut> groups = new ArrayList<>();

    @Schema(description = "Fecha de creación del registro", example = "2026-02-16T10:15:30Z")
    private Instant createdAt;

    @Schema(description = "Fecha de última actualización del registro", example = "2026-02-16T11:05:00Z")
    private Instant updatedAt;

    @Schema(description = "Usuario que creó el registro", example = "admin@dominio.com")
    private String createdBy;

    @Schema(description = "Usuario que realizó la última actualización", example = "usuario@dominio.com")
    private String updatedBy;
}
