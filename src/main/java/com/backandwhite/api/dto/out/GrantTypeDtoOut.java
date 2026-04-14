package com.backandwhite.api.dto.out;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.*;

@Data
@With
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class GrantTypeDtoOut {

    @Schema(description = "Unique identifier of the grant type", example = "1", minimum = "1")
    private Long id;

    @Schema(description = "OAuth2 grant type (authorization_code, implicit, password, client_credentials, refresh_token, etc.)", example = "authorization_code", maxLength = 50, allowableValues = {
            "authorization_code", "implicit", "password", "client_credentials", "refresh_token",
            "urn:ietf:params:oauth:grant-type:jwt-bearer"})
    private String value;

    @Schema(description = "Indicates whether this grant type is enabled", example = "true")
    private Boolean enabled;

    @Schema(description = "Record creation date", example = "2026-02-16T10:15:30Z")
    private Instant createdAt;

    @Schema(description = "Record last update date", example = "2026-02-16T11:05:00Z")
    private Instant updatedAt;

    @Schema(description = "User who created the record", example = "admin@domain.com")
    private String createdBy;

    @Schema(description = "User who performed the last update", example = "user@domain.com")
    private String updatedBy;
}
