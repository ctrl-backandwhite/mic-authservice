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
    @Schema(description = "OAuth2 grant type (authorization_code, implicit, password, client_credentials, refresh_token, etc.)", example = "authorization_code", maxLength = 50, allowableValues = {
            "authorization_code", "implicit", "password", "client_credentials", "refresh_token",
            "urn:ietf:params:oauth:grant-type:jwt-bearer"})
    private String value;

    @NotNull(message = "The grant type status cannot be null")
    @Schema(description = "Indicates whether this grant type is enabled in the OAuth2 server", example = "true", defaultValue = "true")
    private Boolean enabled;
}
