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
public class ScopeDtoIn {

    @NotEmpty
    @Schema(description = "Name of the scope. Must be unique and descriptive. Example: 'read_user_profile'", example = "read_user_profile", minLength = 3, maxLength = 100)
    private String name;

    @NotEmpty
    @Schema(description = "Unique name of the scope in snake_case format. Used internally to identify the scope. Example: 'READ_USER_PROFILE'", example = "READ_USER_PROFILE", minLength = 3, maxLength = 100)
    private String uniqueName;

    @Schema(description = "Detailed description of the scope explaining what permissions it grants. Example: 'Allows reading the user profile'", example = "Allows reading the user profile including personal and contact data", maxLength = 500)
    private String description;

    @NotNull(message = "The scope status cannot be null")
    @Schema(description = "Indicates whether the scope is active and available for use. true = active, false = inactive", example = "true", defaultValue = "true")
    private Boolean enabled;
}
