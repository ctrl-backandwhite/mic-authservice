package com.backandwhite.api.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@With
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RoleDtoIn {

    @NotEmpty
    @Schema(description = "Nombre del rol. Debe ser único y descriptivo. Ejemplo: 'Administrador'", example = "Administrador", minLength = 3, maxLength = 100)
    private String name;

    @NotEmpty
    @Pattern(regexp = "^ROLE_[A-Z0-9_]+$", message = "El nombre único debe seguir el formato ROLE_* (ej: ROLE_ADMIN, ROLE_USER)")
    @Schema(description = "Nombre único del rol en formato snake_case. Se utiliza internamente. Ejemplo: 'ROLE_ADMIN'", example = "ROLE_ADMIN", minLength = 3, maxLength = 100)
    private String uniqueName;

    @Schema(description = "Descripción detallada del rol y sus responsabilidades", example = "Acceso completo a todas las funcionalidades del sistema", maxLength = 500)
    private String description;

    @NotNull(message = "El estado del rol no puede ser nulo")
    @Schema(description = "Indica si el rol está activo y disponible para asignar a usuarios", example = "true", defaultValue = "true")
    private Boolean enabled;
}
