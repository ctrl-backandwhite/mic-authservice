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
public class GrantTypeDtoIn {

    @NotEmpty
    @Schema(
        description = "Tipo de concesión OAuth2 (authorization_code, implicit, password, client_credentials, refresh_token, etc.)",
        example = "authorization_code",
        maxLength = 50,
        allowableValues = {"authorization_code", "implicit", "password", "client_credentials", "refresh_token", "urn:ietf:params:oauth:grant-type:jwt-bearer"}
    )
    private String value;

    @NotNull(message = "El estado del tipo de concesión no puede ser nulo")
    @Schema(
        description = "Indica si este tipo de concesión está habilitado en el servidor OAuth2",
        example = "true",
        defaultValue = "true"
    )
    private Boolean enabled;
}
