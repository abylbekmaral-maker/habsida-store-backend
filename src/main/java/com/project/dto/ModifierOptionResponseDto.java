package com.project.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ModifierOptionResponseDto(
        UUID id,
        String name,
        BigDecimal price
) {}
