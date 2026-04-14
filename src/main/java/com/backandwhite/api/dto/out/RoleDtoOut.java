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
public class RoleDtoOut {

    @Schema(description = "Unique identifier of the role", example = "1", minimum = "1")
    private Long id;

    @Schema(description = "Name of the role", example = "Administrator", minLength = 3, maxLength = 100)
    private String name;

    @Schema(description = "Unique name of the role in snake_case format", example = "ADMIN", minLength = 3, maxLength = 100)
    private String uniqueName;

    @Schema(description = "Detailed description of the role", example = "Full access to all system features", maxLength = 500)
    private String description;

    @Schema(description = "Indicates whether the role is active", example = "true")
    private Boolean enabled;

    @JsonIgnoreProperties({"createdAt", "updatedAt", "createdBy", "updatedBy"})
    @ArraySchema(schema = @Schema(implementation = PermissionDtoOut.class))
    private List<PermissionDtoOut> permissions = new ArrayList<>();

    @Schema(description = "Record creation date", example = "2026-02-16T10:15:30Z")
    private Instant createdAt;

    @Schema(description = "Record last update date", example = "2026-02-16T11:05:00Z")
    private Instant updatedAt;

    @Schema(description = "User who created the record", example = "admin@domain.com")
    private String createdBy;

    @Schema(description = "User who performed the last update", example = "user@domain.com")
    private String updatedBy;
}
