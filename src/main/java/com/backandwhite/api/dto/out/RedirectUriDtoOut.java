package com.backandwhite.api.dto.out;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;




@Data
@With
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RedirectUriDtoOut {

    @Schema(
        description = "Identificador único del URI de redirección",
        example = "1",
        minimum = "1"
    )
    private Long id;

    @Schema(
        description = "Nombre descriptivo del URI de redirección",
        example = "Redirect URI Producción",
        maxLength = 100
    )
    private String name;

    @Schema(
        description = "URI completa de redirección autorizada por OAuth2",
        example = "https://miapp.ejemplo.com/oauth/callback",
        maxLength = 500
    )
    private String value;

    @Schema(
        description = "Indica si este URI de redirección está habilitado",
        example = "true"
    )
    private Boolean enabled;
}
