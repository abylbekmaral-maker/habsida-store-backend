package com.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record StoreBreakRequestDto(

        @NotNull
        @Schema(type = "string", example = "15:00:00")
        LocalTime startTime,

        @NotNull
        @Schema(type = "string", example = "16:00:00")
        LocalTime endTime
) {
}