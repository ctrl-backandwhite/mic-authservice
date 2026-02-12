package com.backandwhite.api.dto.out;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;




@Data
@With
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class GrantTypeDtoOut {

    @Schema(
        description = "Identificador único del tipo de concesión",
        example = "1",
        minimum = "1"
    )
    private Long id;

    @Schema(
        description = "Tipo de concesión OAuth2 (authorization_code, implicit, password, client_credentials, refresh_token, etc.)",
        example = "authorization_code",
        maxLength = 50,
        allowableValues = {"authorization_code", "implicit", "password", "client_credentials", "refresh_token", "urn:ietf:params:oauth:grant-type:jwt-bearer"}
    )
    private String value;

    @Schema(
        description = "Indica si este tipo de concesión está habilitado",
        example = "true"
    )
    private Boolean enabled;
}
