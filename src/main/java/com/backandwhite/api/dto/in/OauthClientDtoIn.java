package com.backandwhite.api.dto.in;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.*;

@Data
@With
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class OauthClientDtoIn {

    @NotEmpty
    @Schema(description = "Unique ID of the OAuth2 client. Used to identify the client application", example = "my-web-app", minLength = 3, maxLength = 100)
    private String clientId;

    @NotEmpty
    @Schema(description = "OAuth2 client secret. Must be kept confidential and never exposed to the browser", example = "abc123xyz789secret", minLength = 8, maxLength = 255)
    private String clientSecret;

    @ArraySchema(schema = @Schema(description = "Scope ID", example = "1", minimum = "1"), arraySchema = @Schema(description = "List of scope IDs the client can request", example = "[1, 2, 3]"))
    private List<Long> scopeIds;

    @ArraySchema(schema = @Schema(description = "Redirect URI ID", example = "1", minimum = "1"), arraySchema = @Schema(description = "List of authorized redirect URI IDs", example = "[1, 2]"))
    private List<Long> redirectUriIds;

    @ArraySchema(schema = @Schema(description = "Grant type ID", example = "1", minimum = "1"), arraySchema = @Schema(description = "List of allowed OAuth2 grant type IDs (authorization_code, implicit, etc.)", example = "[1, 2]"))
    private List<Long> grantTypeIds;
}
