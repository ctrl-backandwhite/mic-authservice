package com.backandwhite.api.dto.out;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;

@Data
@With
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDtoOut {

    @Schema(description = "Identificador único del permiso", example = "1", minimum = "1")
    private Long id;

    @Schema(description = "Nombre del permiso", example = "Leer usuarios", minLength = 3, maxLength = 100)
    private String name;

    @Schema(description = "Nombre único del permiso en formato snake_case", example = "READ_USERS", minLength = 3, maxLength = 100)
    private String uniqueName;

    @Schema(description = "Descripción detallada del permiso", example = "Permite leer la lista de usuarios del sistema", maxLength = 500)
    private String description;

    @Schema(description = "Indica si el permiso está activo", example = "true")
    private Boolean enabled;

    @Schema(description = "Fecha de creación del registro", example = "2026-02-16T10:15:30Z")
    private Instant createdAt;

    @Schema(description = "Fecha de última actualización del registro", example = "2026-02-16T11:05:00Z")
    private Instant updatedAt;

    @Schema(description = "Usuario que creó el registro", example = "admin@dominio.com")
    private String createdBy;

    @Schema(description = "Usuario que realizó la última actualización", example = "usuario@dominio.com")
    private String updatedBy;
}
