package com.backandwhite.api.dto.out;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@With
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ScopeDtoOut {

    @Schema(
        description = "Identificador único del scope. Se genera automáticamente en la base de datos",
        example = "1",
        minimum = "1"
    )
    private Long id;

    @Schema(
        description = "Nombre del scope. Debe ser único y descriptivo. Ejemplo: 'read_user_profile'",
        example = "read_user_profile",
        minLength = 3,
        maxLength = 100
    )
    private String name;

    @Schema(
        description = "Nombre único del scope en formato snake_case. Se utiliza internamente para identificar el scope. Ejemplo: 'READ_USER_PROFILE'",
        example = "READ_USER_PROFILE",
        minLength = 3,
        maxLength = 100
    )
    private String uniqueName;

    @Schema(
        description = "Descripción detallada del scope que explica qué permisos otorga. Ejemplo: 'Permite lectura del perfil del usuario'",
        example = "Permite lectura del perfil del usuario incluyendo datos personales y de contacto",
        maxLength = 500
    )
    private String description;

    @Schema(
        description = "Indica si el scope está activo y disponible para ser utilizado. true = activo, false = inactivo",
        example = "true"
    )
    private Boolean enabled;
}
