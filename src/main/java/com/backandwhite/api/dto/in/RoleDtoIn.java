package com.backandwhite.api.dto.in;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.*;

@Data
@With
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RoleDtoIn {

    @NotEmpty
    @Schema(description = "Name of the role. Must be unique and descriptive. Example: 'Administrator'", example = "Administrator", minLength = 3, maxLength = 100)
    private String name;

    @NotEmpty
    @Pattern(regexp = "^ROLE_[A-Z0-9_]+$", message = "The unique name must follow the ROLE_* format (e.g.: ROLE_ADMIN, ROLE_USER)")
    @Schema(description = "Unique name of the role in snake_case format. Used internally. Example: 'ROLE_ADMIN'", example = "ROLE_ADMIN", minLength = 3, maxLength = 100)
    private String uniqueName;

    @Schema(description = "Detailed description of the role and its responsibilities", example = "Full access to all system features", maxLength = 500)
    private String description;

    @NotNull(message = "The role status cannot be null")
    @Schema(description = "Indicates whether the role is active and available to assign to users", example = "true", defaultValue = "true")
    private Boolean enabled;

    @ArraySchema(schema = @Schema(description = "Permission ID to assign", example = "1", minimum = "1"), arraySchema = @Schema(description = "List of permission IDs for the role", example = "[1, 2, 3]"))
    private List<Long> permissionIds;
}
