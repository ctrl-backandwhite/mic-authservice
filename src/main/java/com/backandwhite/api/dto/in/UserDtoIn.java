package com.backandwhite.api.dto.in;

import com.backandwhite.api.validation.CreateValidation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
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
public class UserDtoIn {

    @NotEmpty
    @Schema(description = "First name of the user", example = "John", minLength = 2, maxLength = 100)
    private String name;

    @NotEmpty
    @Schema(description = "Last name of the user", example = "Smith", minLength = 2, maxLength = 100)
    private String lastName;

    @Schema(description = "Unique username for login. Letters, numbers and hyphens only", example = "john.smith", minLength = 3, maxLength = 50)
    private String nickName;

    @NotEmpty
    @Email
    @Schema(description = "User's email address (must be unique)", example = "john.smith@example.com")
    private String email;

    @Schema(description = "User password (minimum 8 characters, must contain uppercase, lowercase and numbers)", example = "MyPassword123!", minLength = 8, maxLength = 255)
    @NotEmpty(message = "Password is required", groups = CreateValidation.class)
    private String password;

    @Schema(description = "User password confirmation (minimum 8 characters, must contain uppercase, lowercase and numbers)", example = "MyPassword123!", minLength = 8, maxLength = 255)
    @NotEmpty(message = "Password confirmation is required", groups = CreateValidation.class)
    private String confirmPassword;

    @NotNull
    @Schema(description = "Indicates whether the user account is enabled and active", example = "true", defaultValue = "true")
    private Boolean enabled;

    @NotNull(message = "The accountNonExpired field cannot be null")
    @Schema(description = "Indicates whether the user account has not expired", example = "true", defaultValue = "true")
    private Boolean accountNonExpired;

    @NotNull(message = "The accountNonLocked field cannot be null")
    @Schema(description = "Indicates whether the user account is not locked", example = "true", defaultValue = "true")
    private Boolean accountNonLocked;

    @NotNull(message = "The credentialsNonExpired field cannot be null")
    @Schema(description = "Indicates whether the user credentials have not expired", example = "true", defaultValue = "true")
    private Boolean credentialsNonExpired;

    @ArraySchema(schema = @Schema(description = "Scope ID", example = "1", minimum = "1"), arraySchema = @Schema(description = "List of scope IDs assigned to the user", example = "[1, 2, 3]"))
    private List<Long> scopeIds;

    @ArraySchema(schema = @Schema(description = "Role ID", example = "1", minimum = "1"), arraySchema = @Schema(description = "List of role IDs directly assigned to the user", example = "[1, 2]"))
    private List<Long> roleIds;

    @ArraySchema(schema = @Schema(description = "Group ID", example = "1", minimum = "1"), arraySchema = @Schema(description = "List of group IDs the user belongs to", example = "[1]"))
    private List<Long> groupIds;

    @AssertTrue(message = "Password and confirmation do not match", groups = CreateValidation.class)
    public boolean isPasswordConfirmed() {
        if (password == null || confirmPassword == null) {
            return false;
        }
        return password.equals(confirmPassword);
    }
}
