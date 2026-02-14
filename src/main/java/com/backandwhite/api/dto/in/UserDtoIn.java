package com.backandwhite.api.dto.in;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import com.backandwhite.api.validation.CreateValidation;

import java.util.List;

@Data
@With
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoIn {

    @NotEmpty
    @Schema(description = "Nombre del usuario", example = "Juan", minLength = 2, maxLength = 100)
    private String name;

    @NotEmpty
    @Schema(description = "Apellido del usuario", example = "Pérez García", minLength = 2, maxLength = 100)
    private String lastName;

    @Schema(description = "Nombre de usuario único para login. Solo letras, números y guiones", example = "juan.perez", minLength = 3, maxLength = 50)
    private String nickName;

    @NotEmpty
    @Email
    @Schema(description = "Dirección de correo electrónico del usuario (debe ser única)", example = "juan.perez@ejemplo.com")
    private String email;

    @Schema(description = "Contraseña del usuario (mínimo 8 caracteres, debe contener mayúsculas, minúsculas y números)", example = "MiPassword123!", minLength = 8, maxLength = 255)
    @NotEmpty(message = "La contraseña es obligatoria", groups = CreateValidation.class)
    private String password;

    @Schema(description = "Contraseña del usuario (mínimo 8 caracteres, debe contener mayúsculas, minúsculas y números)", example = "MiPassword123!", minLength = 8, maxLength = 255)
    @NotEmpty(message = "La confirmación de contraseña es obligatoria", groups = CreateValidation.class)
    private String confirmPassword;

    @NotNull
    @Schema(description = "Indica si la cuenta del usuario está habilitada y activa", example = "true", defaultValue = "true")
    private Boolean enabled;

    @NotNull(message = "El campo accountNonExpired no puede ser nulo")
    @Schema(description = "Indica si la cuenta del usuario no ha expirado", example = "true", defaultValue = "true")
    private Boolean accountNonExpired;

    @NotNull(message = "El campo accountNonLocked no puede ser nulo")
    @Schema(description = "Indica si la cuenta del usuario no está bloqueada", example = "true", defaultValue = "true")
    private Boolean accountNonLocked;

    @NotNull(message = "El campo credentialsNonExpired no puede ser nulo")
    @Schema(description = "Indica si las credenciales (contraseña) del usuario no han expirado", example = "true", defaultValue = "true")
    private Boolean credentialsNonExpired;

    @ArraySchema(schema = @Schema(description = "ID del scope", example = "1", minimum = "1"), arraySchema = @Schema(description = "Lista de IDs de scopes asignados al usuario", example = "[1, 2, 3]"))
    private List<Long> scopeIds;

    @ArraySchema(schema = @Schema(description = "ID del rol", example = "1", minimum = "1"), arraySchema = @Schema(description = "Lista de IDs de roles asignados directamente al usuario", example = "[1, 2]"))
    private List<Long> roleIds;

    @ArraySchema(schema = @Schema(description = "ID del grupo", example = "1", minimum = "1"), arraySchema = @Schema(description = "Lista de IDs de grupos a los que pertenece el usuario", example = "[1]"))
    private List<Long> groupIds;

    @AssertTrue(message = "La contraseña y su confirmación no coinciden", groups = CreateValidation.class)
    public boolean isPasswordConfirmed() {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }
}
