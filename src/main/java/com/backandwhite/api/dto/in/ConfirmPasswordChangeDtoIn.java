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

    @NotBlank(message = "El código de verificación es obligatorio.")
    @Size(min = 6, max = 6, message = "El código debe tener exactamente 6 dígitos.")
    @Schema(description = "Código de verificación de 6 dígitos enviado al correo.", example = "482951")
    private String code;
}
