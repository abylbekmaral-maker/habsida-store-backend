package com.project.dto;

import java.time.LocalTime;
import java.util.UUID;

public record StoreBreakResponseDto(
        UUID id,
        LocalTime startTime,
        LocalTime endTime
) {
}