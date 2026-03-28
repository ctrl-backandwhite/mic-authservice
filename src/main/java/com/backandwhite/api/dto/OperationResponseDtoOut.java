package com.backandwhite.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationResponseDtoOut {

    @Schema(description = "Código interno de la operación", example = "VALIDATION_ERROR")
    private String code;

    @Schema(description = "Mensaje descriptivo del resultado", example = "Si el correo está registrado, recibirás un enlace.")
    private String message;

    @Schema(description = "Lista de detalles adicionales o errores de validación")
    private List<String> details;

    @Schema(description = "Fecha y hora en que ocurrió la respuesta", example = "2026-03-28T10:15:30+00:00")
    private ZonedDateTime dateTime;
}
