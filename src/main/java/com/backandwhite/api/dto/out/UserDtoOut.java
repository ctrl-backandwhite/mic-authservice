package com.backandwhite.api.dto.out;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class UserDtoOut {

    @Schema(description = "Unique identifier of the user", example = "1", minimum = "1")
    private Long id;

    @Schema(description = "First name of the user", example = "John", minLength = 2, maxLength = 100)
    private String name;

    @Schema(description = "Last name of the user", example = "Smith", minLength = 2, maxLength = 100)
    private String lastName;

    @Schema(description = "Unique username for login", example = "john.smith", minLength = 3, maxLength = 50)
    private String nickName;

    @Schema(description = "User's email address", example = "john.smith@example.com")
    private String email;

    @JsonIgnore
    @Schema(description = "User password (not returned in responses for security)", example = "***", hidden = true)
    private String password;

    @Schema(description = "Indicates whether the user account is enabled", example = "true")
    private Boolean enabled;

    @Schema(description = "Indicates whether the user account has not expired", example = "true")
    private Boolean accountNonExpired;

    @Schema(description = "Indicates whether the user account is not locked", example = "true")
    private Boolean accountNonLocked;

    @Schema(description = "Indicates whether the user credentials have not expired", example = "true")
    private Boolean credentialsNonExpired;

    @JsonIgnoreProperties({"createdAt", "updatedAt", "createdBy", "updatedBy"})
    @ArraySchema(schema = @Schema(implementation = RoleDtoOut.class), arraySchema = @Schema(description = "Roles directly assigned to the user"))
    private List<RoleDtoOut> roles = new ArrayList<>();

    @JsonIgnoreProperties({"createdAt", "updatedAt", "createdBy", "updatedBy"})
    @ArraySchema(schema = @Schema(implementation = GroupDtoOut.class), arraySchema = @Schema(description = "Groups the user belongs to"))
    private List<GroupDtoOut> groups = new ArrayList<>();

    @Schema(description = "Record creation date", example = "2026-02-16T10:15:30Z")
    private Instant createdAt;

    @Schema(description = "Record last update date", example = "2026-02-16T11:05:00Z")
    private Instant updatedAt;

    @Schema(description = "User who created the record", example = "admin@domain.com")
    private String createdBy;

    @Schema(description = "User who performed the last update", example = "user@domain.com")
    private String updatedBy;
}
