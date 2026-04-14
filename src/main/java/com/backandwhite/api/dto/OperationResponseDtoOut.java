package com.backandwhite.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.*;

@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationResponseDtoOut {

    @Schema(description = "Internal operation code", example = "VALIDATION_ERROR")
    private String code;

    @Schema(description = "Descriptive message of the result", example = "If the email is registered, you will receive a link.")
    private String message;

    @Schema(description = "List of additional details or validation errors")
    private List<String> details;

    @Schema(description = "Date and time when the response occurred", example = "2026-03-28T10:15:30+00:00")
    private ZonedDateTime dateTime;
}
