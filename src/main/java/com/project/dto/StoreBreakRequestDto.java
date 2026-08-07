package com.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public record StoreBreakRequestDto(

        @NotNull
        @Schema(type = "string", example = "15:00:00")
        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime startTime,

        @NotNull
        @Schema(type = "string", example = "16:00:00")
        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime endTime
) {
}