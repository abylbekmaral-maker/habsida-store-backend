package com.project.dto;

import com.project.entity.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record StoreHourResponseDto(
        UUID id,
        DayOfWeek dayOfWeek,
        LocalTime openTime,
        LocalTime closeTime,
        boolean closed,
        LocalTime lastOrderCutoffTime,
        List<StoreBreakResponseDto> breaks
) {
}