package com.project.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.entity.DayOfWeek;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

public record StoreHourRequestDto(

        @NotNull
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

        @Valid
        List<StoreBreakRequestDto> breaks
) {
}