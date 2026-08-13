package com.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

@Schema(description = "Order line item details")
public record OrderItemResponseDto(

        @Schema(description = "Unique order item ID", example = "f1b2c3d4-e7a8b-1e2fb")
        UUID id,

        @Schema(description = "Product ID", example = "d29c35f6-3dcf-cfd8aac1")
        UUID productId,

        @Schema(description = "Snapshot of the product name at the time of purchase", example = "Antonovka Apple")
        String productNameSnapshot,

        @Schema(description = "Snapshot of the product price at the time of purchase", example = "5.50")
        BigDecimal productPriceSnapshot,

        @Schema(description = "Quantity ordered", example = "2")
        Integer quantity,

        @Schema(description = "Line total calculation for this item including options", example = "11.00")
        BigDecimal lineTotal,

        @Schema(description = "Selected modifiers for this item")
        List<OrderItemModifierResponseDto> modifiers
) {}
