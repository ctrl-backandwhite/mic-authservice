package com.backandwhite.api.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;



@Data
@With
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ScopeDtoIn {

    @NotEmpty
    @Schema(
        description = "Nombre del scope. Debe ser único y descriptivo. Ejemplo: 'read_user_profile'",
        example = "read_user_profile",
        minLength = 3,
        maxLength = 100
    )
    private String name;

    @NotEmpty
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

    @NotNull(message = "El estado del scope no puede ser nulo")
    @Schema(
        description = "Indica si el scope está activo y disponible para ser utilizado. true = activo, false = inactivo",
        example = "true",
        defaultValue = "true"
    )
    private Boolean enabled;
}
