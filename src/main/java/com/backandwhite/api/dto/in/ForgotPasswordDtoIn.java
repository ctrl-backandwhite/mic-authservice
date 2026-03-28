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

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "El correo no tiene un formato válido.")
    @Schema(description = "Correo electrónico del usuario para recuperar la contraseña.", example = "usuario@ejemplo.com")
    private String email;
}
