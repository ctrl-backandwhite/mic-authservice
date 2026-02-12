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
public class RedirectUriDtoIn {

    @NotEmpty
    @Schema(
        description = "Nombre descriptivo del URI de redirección (ej: Producción, Desarrollo, Testing)",
        example = "Redirect URI Producción",
        maxLength = 100
    )
    private String name;

    @NotEmpty
    @Schema(
        description = "URI completa de redirección autorizada por OAuth2. Debe ser HTTPS en producción",
        example = "https://miapp.ejemplo.com/oauth/callback",
        maxLength = 500
    )
    private String value;

    @NotNull(message = "El estado del URI de redirección no puede ser nulo")
    @Schema(
        description = "Indica si este URI de redirección está habilitado para ser utilizado",
        example = "true",
        defaultValue = "true"
    )
    private Boolean enabled;
}
