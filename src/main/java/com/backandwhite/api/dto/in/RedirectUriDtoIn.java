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
    @Schema(description = "Descriptive name of the redirect URI (e.g.: Production, Development, Testing)", example = "Redirect URI Production", maxLength = 100)
    private String name;

    @NotEmpty
    @Schema(description = "Full redirect URI authorized by OAuth2. Must be HTTPS in production", example = "https://myapp.example.com/oauth/callback", maxLength = 500)
    private String value;

    @NotNull(message = "The redirect URI status cannot be null")
    @Schema(description = "Indicates whether this redirect URI is enabled for use", example = "true", defaultValue = "true")
    private Boolean enabled;
}
