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
public class ConfirmPasswordChangeDtoIn {

    @NotBlank(message = "The verification code is required.")
    @Size(min = 6, max = 6, message = "The code must be exactly 6 digits.")
    @Schema(description = "6-digit verification code sent to the email.", example = "482951")
    private String code;
}
