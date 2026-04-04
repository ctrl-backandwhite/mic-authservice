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
public class RevokeSessionRequestDtoIn {

    @NotBlank(message = "El identificador de sesión es requerido")
    @Size(max = 64)
    private String sessionId;
}
