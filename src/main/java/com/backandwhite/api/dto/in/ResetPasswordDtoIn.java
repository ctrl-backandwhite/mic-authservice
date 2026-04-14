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
public class ResetPasswordDtoIn {

    @NotBlank(message = "The recovery token is required.")
    @Schema(description = "Password recovery token sent by email.", example = "a1b2c3d4e5f6...")
    private String token;

    @NotBlank(message = "The new password is required.")
    @Size(min = 8, message = "Password must be at least 8 characters.")
    @Schema(description = "New user password. Minimum 8 characters.", example = "MyNewPass1")
    private String newPassword;
}
