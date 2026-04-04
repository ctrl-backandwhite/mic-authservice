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

    @NotBlank(message = "El código de verificación es requerido")
    @Size(min = 6, max = 6, message = "El código debe tener 6 dígitos")
    private String code;
}
