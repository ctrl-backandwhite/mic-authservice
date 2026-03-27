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
    @Schema(description = "Nombre del permiso. Debe ser único y descriptivo. Ejemplo: 'Leer usuarios'", example = "Leer usuarios", minLength = 3, maxLength = 100)
    private String name;

    @NotEmpty
    @Schema(description = "Nombre único del permiso en formato snake_case. Se utiliza internamente. Ejemplo: 'READ_USERS'", example = "READ_USERS", minLength = 3, maxLength = 100)
    private String uniqueName;

    @Schema(description = "Descripción detallada del permiso y su alcance", example = "Permite leer la lista de usuarios del sistema", maxLength = 500)
    private String description;

    @NotNull(message = "El estado del permiso no puede ser nulo")
    @Schema(description = "Indica si el permiso está activo y disponible para asignar", example = "true", defaultValue = "true")
    private Boolean enabled;
}
