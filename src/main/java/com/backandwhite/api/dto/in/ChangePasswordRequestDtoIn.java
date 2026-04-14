package com.backandwhite.api.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDtoIn {

    @NotBlank(message = "Current password is required.")
    @Schema(description = "Current user password.", example = "MyCurrentPass1")
    private String currentPassword;

    @NotBlank(message = "New password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters.")
    @Schema(description = "New user password. Minimum 8 characters.", example = "MyNewPass1")
    private String newPassword;

    @NotBlank(message = "Password confirmation is required.")
    @Size(min = 8, message = "Password must be at least 8 characters.")
    @Schema(description = "Confirmation of the new password.", example = "MyNewPass1")
    private String confirmPassword;
}
