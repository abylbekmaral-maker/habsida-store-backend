package com.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.entity.DayOfWeek;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record StoreHourResponseDto(
        UUID id,

        DayOfWeek dayOfWeek,

        @Schema(type = "string", example = "09:00:00")
        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime openTime,

        @Schema(type = "string", example = "22:00:00")
        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime closeTime,

        boolean closed,

        @Schema(type = "string", example = "21:30:00")
        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime lastOrderCutoffTime,

        List<StoreBreakResponseDto> breaks
) {
}