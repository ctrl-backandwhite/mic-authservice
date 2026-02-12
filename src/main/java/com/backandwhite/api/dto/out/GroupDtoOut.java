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
public class GroupDtoOut {

    @Schema(
        description = "Identificador único del grupo",
        example = "1",
        minimum = "1"
    )
    private Long id;

    @Schema(
        description = "Nombre del grupo",
        example = "Gerentes de Ventas",
        minLength = 3,
        maxLength = 100
    )
    private String name;

    @Schema(
        description = "Nombre único del grupo en formato snake_case",
        example = "SALES_MANAGERS",
        minLength = 3,
        maxLength = 100
    )
    private String uniqueName;

    @Schema(
        description = "Descripción del grupo",
        example = "Grupo de usuarios con permiso de gestión de ventas",
        maxLength = 500
    )
    private String description;

    @Schema(
        description = "Indica si el grupo está activo",
        example = "true"
    )
    private Boolean enabled;

    @ArraySchema(
        schema = @Schema(implementation = RoleDtoOut.class),
        arraySchema = @Schema(
            description = "Roles asociados a este grupo"
        )
    )
    private List<RoleDtoOut> roles = new ArrayList<>();
}
