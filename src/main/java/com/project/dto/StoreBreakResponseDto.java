package com.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;
import java.util.UUID;

public record StoreBreakResponseDto(
        UUID id,

        @Schema(type = "string", example = "09:00:00")
        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime startTime,

        LocalTime endTime
) {
}