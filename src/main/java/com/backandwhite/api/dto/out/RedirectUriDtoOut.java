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
public class RedirectUriDtoOut {

    @Schema(description = "Unique identifier of the redirect URI", example = "1", minimum = "1")
    private Long id;

    @Schema(description = "Descriptive name of the redirect URI", example = "Redirect URI Production", maxLength = 100)
    private String name;

    @Schema(description = "Full redirect URI authorized by OAuth2", example = "https://myapp.example.com/oauth/callback", maxLength = 500)
    private String value;

    @Schema(description = "Indicates whether this redirect URI is enabled", example = "true")
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
