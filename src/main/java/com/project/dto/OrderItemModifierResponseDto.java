package com.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Selected modifier item snapshot in an order")
public record OrderItemModifierResponseDto(

        @Schema(description = "Unique order item modifier ID", example = "e1b2c3d4-e5f67a8b-9c0d")
        UUID id,

        @Schema(description = "Modifier option ID", example = "m1b2c3d4-e5f67a8b-9c0d")
        UUID modifierOptionId,

        @Schema(description = "Snapshot of the modifier name at purchase time", example = "Box")
        String modifierNameSnapshot,

        @Schema(description = "Snapshot of the modifier price at purchase time", example = "0.50")
        BigDecimal modifierPriceSnapshot
) {
}