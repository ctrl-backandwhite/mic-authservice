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
public class PermissionDtoIn {

    @NotEmpty
    @Schema(description = "Name of the permission. Must be unique and descriptive. Example: 'Read users'", example = "Read users", minLength = 3, maxLength = 100)
    private String name;

    @NotEmpty
    @Schema(description = "Unique name of the permission in snake_case format. Used internally. Example: 'READ_USERS'", example = "READ_USERS", minLength = 3, maxLength = 100)
    private String uniqueName;

    @Schema(description = "Detailed description of the permission and its scope", example = "Allows reading the list of system users", maxLength = 500)
    private String description;

    @NotNull(message = "The permission status cannot be null")
    @Schema(description = "Indicates whether the permission is active and available to assign", example = "true", defaultValue = "true")
    private Boolean enabled;
}
