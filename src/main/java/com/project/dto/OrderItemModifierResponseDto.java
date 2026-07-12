package com.project.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemModifierResponseDto(
        UUID id,
        UUID modifierOptionId,
        String modifierNameSnapshot,
        BigDecimal modifierPriceSnapshot
) {
}