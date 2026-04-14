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
public class PermissionDtoOut {

    @Schema(description = "Unique identifier of the permission", example = "1", minimum = "1")
    private Long id;

    @Schema(description = "Name of the permission", example = "Read users", minLength = 3, maxLength = 100)
    private String name;

    @Schema(description = "Unique name of the permission in snake_case format", example = "READ_USERS", minLength = 3, maxLength = 100)
    private String uniqueName;

    @Schema(description = "Detailed description of the permission", example = "Allows reading the list of system users", maxLength = 500)
    private String description;

    @Schema(description = "Indicates whether the permission is active", example = "true")
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
