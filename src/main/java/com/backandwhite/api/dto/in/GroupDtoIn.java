package com.backandwhite.api.dto.in;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.*;

@Data
@With
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class GroupDtoIn {

    @NotEmpty
    @Schema(description = "Name of the group. Must be unique and descriptive. Example: 'Sales Managers'", example = "Sales Managers", minLength = 3, maxLength = 100)
    private String name;

    @NotEmpty
    @Schema(description = "Unique name of the group in snake_case format. Example: 'SALES_MANAGERS'", example = "SALES_MANAGERS", minLength = 3, maxLength = 100)
    private String uniqueName;

    @NotEmpty(message = "Description cannot be empty")
    @Schema(description = "Description of the group and its purpose in the organization", example = "Group of users with permission to manage sales and reports", maxLength = 500)
    private String description;

    @NotNull(message = "The group status cannot be null")
    @Schema(description = "Indicates whether the group is active", example = "true", defaultValue = "true")
    private Boolean enabled;

    @ArraySchema(schema = @Schema(description = "Role ID to assign", example = "1", minimum = "1"), arraySchema = @Schema(description = "List of role IDs that belong to this group.", example = "[1, 2, 3]"))
    private List<Long> roleIds;

    @ArraySchema(schema = @Schema(description = "Permission ID to assign", example = "1", minimum = "1"), arraySchema = @Schema(description = "List of permission IDs for the group", example = "[1, 2, 3]"))
    private List<Long> permissionIds;
}
