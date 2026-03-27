package com.backandwhite.api.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;


@Data
@With
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class GroupDtoIn {

    @NotEmpty
    @Schema(
        description = "Nombre del grupo. Debe ser único y descriptivo. Ejemplo: 'Gerentes de Ventas'",
        example = "Gerentes de Ventas",
        minLength = 3,
        maxLength = 100
    )
    private String name;

    @NotEmpty
    @Schema(
        description = "Nombre único del grupo en formato snake_case. Ejemplo: 'SALES_MANAGERS'",
        example = "SALES_MANAGERS",
        minLength = 3,
        maxLength = 100
    )
    private String uniqueName;

    @NotEmpty(message = "La descripción no puede estar vacía")
    @Schema(
        description = "Descripción del grupo y su propósito en la organización",
        example = "Grupo de usuarios con permiso de gestión de ventas y reportes",
        maxLength = 500
    )
    private String description;

    @NotNull(message = "El estado del grupo no puede ser nulo")
    @Schema(
        description = "Indica si el grupo está activo",
        example = "true",
        defaultValue = "true"
    )
    private Boolean enabled;

    @ArraySchema(
        schema = @Schema(
            description = "ID del rol a asignar",
            example = "1",
            minimum = "1"
        ),
        arraySchema = @Schema(
            description = "Lista de IDs de roles que pertenecen a este grupo.",
            example = "[1, 2, 3]"
        )
    )
    private List<Long> roleIds;

    @ArraySchema(
        schema = @Schema(description = "ID del permiso a asignar", example = "1", minimum = "1"),
        arraySchema = @Schema(description = "Lista de IDs de permisos del grupo", example = "[1, 2, 3]")
    )
    private List<Long> permissionIds;
}
