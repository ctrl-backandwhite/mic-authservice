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

    @NotBlank(message = "La contraseña actual es obligatoria.")
    @Schema(description = "Contraseña actual del usuario.", example = "MiPassActual1")
    private String currentPassword;

    @NotBlank(message = "La nueva contraseña es obligatoria.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    @Schema(description = "Nueva contraseña del usuario. Mínimo 8 caracteres.", example = "MiNuevaPass1")
    private String newPassword;

    @NotBlank(message = "La confirmación de contraseña es obligatoria.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    @Schema(description = "Confirmación de la nueva contraseña.", example = "MiNuevaPass1")
    private String confirmPassword;
}
