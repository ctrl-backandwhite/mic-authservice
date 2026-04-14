package com.backandwhite.api.dto.out;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Data
@With
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class OauthClientDtoOut {

    @Schema(description = "Unique identifier of the OAuth2 client", example = "1", minimum = "1")
    private Long id;

    @Schema(description = "Unique ID of the OAuth2 client. Used to identify the client application", example = "my-web-app", minLength = 3, maxLength = 100)
    private String clientId;

    @Schema(description = "OAuth2 client secret. Must be kept confidential", example = "abc123xyz789secret", minLength = 8, maxLength = 255)
    private String clientSecret;

    @JsonIgnoreProperties({"createdAt", "updatedAt", "createdBy", "updatedBy"})
    @ArraySchema(schema = @Schema(implementation = ScopeDtoOut.class), arraySchema = @Schema(description = "Scopes the client can request"))
    private List<ScopeDtoOut> scopes = new ArrayList<>();

    @JsonIgnoreProperties({"createdAt", "updatedAt", "createdBy", "updatedBy"})
    @ArraySchema(schema = @Schema(implementation = RedirectUriDtoOut.class), arraySchema = @Schema(description = "Authorized redirect URIs"))
    private List<RedirectUriDtoOut> redirectUris = new ArrayList<>();

    @JsonIgnoreProperties({"createdAt", "updatedAt", "createdBy", "updatedBy"})
    @ArraySchema(schema = @Schema(implementation = GrantTypeDtoOut.class), arraySchema = @Schema(description = "Allowed OAuth2 grant types"))
    private List<GrantTypeDtoOut> grantTypes = new ArrayList<>();

    @Schema(description = "Record creation date", example = "2026-02-16T10:15:30Z")
    private Instant createdAt;

    @Schema(description = "Record last update date", example = "2026-02-16T11:05:00Z")
    private Instant updatedAt;

    @Schema(description = "User who created the record", example = "admin@domain.com")
    private String createdBy;

    @Schema(description = "User who performed the last update", example = "user@domain.com")
    private String updatedBy;
}
