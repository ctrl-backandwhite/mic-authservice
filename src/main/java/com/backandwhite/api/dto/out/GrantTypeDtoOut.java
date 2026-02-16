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
public class GrantTypeDtoOut {

    @Schema(description = "Identificador único del tipo de concesión", example = "1", minimum = "1")
    private Long id;

    @Schema(description = "Fecha de creación del registro", example = "2026-02-16T10:15:30Z")
    private Instant createdAt;

    @Schema(description = "Fecha de última actualización del registro", example = "2026-02-16T11:05:00Z")
    private Instant updatedAt;

    @Schema(description = "Usuario que creó el registro", example = "admin@dominio.com")
    private String createdBy;

    @Schema(description = "Usuario que realizó la última actualización", example = "usuario@dominio.com")
    private String updatedBy;

    @Schema(description = "Tipo de concesión OAuth2 (authorization_code, implicit, password, client_credentials, refresh_token, etc.)", example = "authorization_code", maxLength = 50, allowableValues = {
            "authorization_code", "implicit", "password", "client_credentials", "refresh_token",
            "urn:ietf:params:oauth:grant-type:jwt-bearer" })
    private String value;

    @Schema(description = "Indica si este tipo de concesión está habilitado", example = "true")
    private Boolean enabled;
}
