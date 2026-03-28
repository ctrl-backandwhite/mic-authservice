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

    @NotBlank(message = "El token de recuperación es obligatorio.")
    @Schema(description = "Token de recuperación de contraseña enviado por correo.", example = "a1b2c3d4e5f6...")
    private String token;

    @NotBlank(message = "La nueva contraseña es obligatoria.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    @Schema(description = "Nueva contraseña del usuario. Mínimo 8 caracteres.", example = "MiNuevaPass1")
    private String newPassword;
}
