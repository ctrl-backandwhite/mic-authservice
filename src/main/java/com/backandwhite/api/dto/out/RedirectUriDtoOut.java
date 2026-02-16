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
public class RedirectUriDtoOut {

    @Schema(description = "Identificador único del URI de redirección", example = "1", minimum = "1")
    private Long id;

    @Schema(description = "Nombre descriptivo del URI de redirección", example = "Redirect URI Producción", maxLength = 100)
    private String name;

    @Schema(description = "URI completa de redirección autorizada por OAuth2", example = "https://miapp.ejemplo.com/oauth/callback", maxLength = 500)
    private String value;

    @Schema(description = "Indica si este URI de redirección está habilitado", example = "true")
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
