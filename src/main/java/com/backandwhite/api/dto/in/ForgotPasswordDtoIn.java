package com.backandwhite.api.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordDtoIn {

    @NotBlank(message = "Email is required.")
    @Email(message = "Email format is not valid.")
    @Schema(description = "User's email address to recover the password.", example = "user@example.com")
    private String email;
}
