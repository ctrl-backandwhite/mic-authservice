package com.backandwhite.api.dto.out;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;




@Data
@With
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RoleDtoOut {

    @Schema(
        description = "Identificador único del rol",
        example = "1",
        minimum = "1"
    )
    private Long id;

    @Schema(
        description = "Nombre del rol",
        example = "Administrador",
        minLength = 3,
        maxLength = 100
    )
    private String name;

    @Schema(
        description = "Nombre único del rol en formato snake_case",
        example = "ADMIN",
        minLength = 3,
        maxLength = 100
    )
    private String uniqueName;

    @Schema(
        description = "Descripción detallada del rol",
        example = "Acceso completo a todas las funcionalidades del sistema",
        maxLength = 500
    )
    private String description;

    @Schema(
        description = "Indica si el rol está activo",
        example = "true"
    )
    private Boolean enabled;
}
