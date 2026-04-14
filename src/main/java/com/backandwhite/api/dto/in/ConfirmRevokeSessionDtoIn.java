package com.backandwhite.api.dto.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmRevokeSessionDtoIn {

    @NotBlank(message = "The verification code is required")
    @Size(min = 6, max = 6, message = "The code must be 6 digits")
    private String code;
}
