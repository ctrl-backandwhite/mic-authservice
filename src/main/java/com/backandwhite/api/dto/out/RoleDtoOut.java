package com.backandwhite.api.dto.out;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@With
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RoleDtoOut {

    @Schema(description = "Identificador único del rol", example = "1", minimum = "1")
    private Long id;

    @Schema(description = "Nombre del rol", example = "Administrador", minLength = 3, maxLength = 100)
    private String name;

    @Schema(description = "Nombre único del rol en formato snake_case", example = "ADMIN", minLength = 3, maxLength = 100)
    private String uniqueName;

    @Schema(description = "Descripción detallada del rol", example = "Acceso completo a todas las funcionalidades del sistema", maxLength = 500)
    private String description;

    @Schema(description = "Indica si el rol está activo", example = "true")
    private Boolean enabled;

    @JsonIgnoreProperties({ "createdAt", "updatedAt", "createdBy", "updatedBy" })
    @ArraySchema(schema = @Schema(implementation = PermissionDtoOut.class))
    private List<PermissionDtoOut> permissions = new ArrayList<>();

    @Schema(description = "Fecha de creación del registro", example = "2026-02-16T10:15:30Z")
    private Instant createdAt;

    @Schema(description = "Fecha de última actualización del registro", example = "2026-02-16T11:05:00Z")
    private Instant updatedAt;

    @Schema(description = "Usuario que creó el registro", example = "admin@dominio.com")
    private String createdBy;

    @Schema(description = "Usuario que realizó la última actualización", example = "usuario@dominio.com")
    private String updatedBy;
}
