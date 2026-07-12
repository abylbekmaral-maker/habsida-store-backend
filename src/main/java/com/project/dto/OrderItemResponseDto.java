package com.project.dto;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

public record OrderItemResponseDto(
        UUID id,
        UUID productId,
        String productNameSnapshot,
        BigDecimal productPriceSnapshot,
        Integer quantity,
        BigDecimal lineTotal,
        List<OrderItemModifierResponseDto> modifiers
) {}
