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
public class PermissionDtoIn {

    @NotEmpty
    @Schema(
        description = "Nombre del permiso. Debe ser descriptivo. Ejemplo: 'Crear usuarios'",
        example = "Crear usuarios",
        minLength = 3,
        maxLength = 100
    )
    private String name;

    @NotEmpty
    @Schema(
        description = "Nombre único del permiso. Se utiliza internamente. Ejemplo: 'PERM_CREATE_USER'",
        example = "PERM_CREATE_USER",
        minLength = 3,
        maxLength = 100
    )
    private String uniqueName;

    @Schema(
        description = "Descripción detallada del permiso",
        example = "Permite crear nuevos usuarios en el sistema",
        maxLength = 500
    )
    private String description;

    @NotNull(message = "El estado del permiso no puede ser nulo")
    @Schema(
        description = "Indica si el permiso está activo",
        example = "true",
        defaultValue = "true"
    )
    private Boolean enabled;
}
