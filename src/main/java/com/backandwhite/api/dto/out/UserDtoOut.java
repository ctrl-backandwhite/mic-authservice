package com.backandwhite.api.dto.out;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


import java.util.List;
import java.util.ArrayList;


@Data
@With
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoOut {

    @Schema(
        description = "Identificador único del usuario",
        example = "1",
        minimum = "1"
    )
    private Long id;

    @Schema(
        description = "Nombre del usuario",
        example = "Juan",
        minLength = 2,
        maxLength = 100
    )
    private String name;

    @Schema(
        description = "Apellido del usuario",
        example = "Pérez García",
        minLength = 2,
        maxLength = 100
    )
    private String lastName;

    @Schema(
        description = "Nombre de usuario único para login",
        example = "juan.perez",
        minLength = 3,
        maxLength = 50
    )
    private String nickName;

    @Schema(
        description = "Dirección de correo electrónico del usuario",
        example = "juan.perez@ejemplo.com"
    )
    private String email;

    @Schema(
        description = "Contraseña del usuario (no se retorna en respuestas por seguridad)",
        example = "***",
        hidden = true
    )
    private String password;

    @Schema(
        description = "Indica si la cuenta del usuario está habilitada",
        example = "true"
    )
    private Boolean enabled;

    @Schema(
        description = "Indica si la cuenta del usuario no ha expirado",
        example = "true"
    )
    private Boolean accountNonExpired;

    @Schema(
        description = "Indica si la cuenta del usuario no está bloqueada",
        example = "true"
    )
    private Boolean accountNonLocked;

    @Schema(
        description = "Indica si las credenciales del usuario no han expirado",
        example = "true"
    )
    private Boolean credentialsNonExpired;

    @ArraySchema(
        schema = @Schema(implementation = ScopeDtoOut.class),
        arraySchema = @Schema(
            description = "Scopes directos asignados al usuario"
        )
    )
    private List<ScopeDtoOut> scopes = new ArrayList<>();

    @ArraySchema(
        schema = @Schema(implementation = RoleDtoOut.class),
        arraySchema = @Schema(
            description = "Roles directos asignados al usuario"
        )
    )
    private List<RoleDtoOut> roles = new ArrayList<>();

    @ArraySchema(
        schema = @Schema(implementation = GroupDtoOut.class),
        arraySchema = @Schema(
            description = "Grupos a los que pertenece el usuario"
        )
    )
    private List<GroupDtoOut> groups = new ArrayList<>();
}
