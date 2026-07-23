package com.project.dto;

import com.project.entity.OrderStatus;
import com.project.entity.OrderType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

public record OrderResponseDto(

        @Schema(description = "Unique order ID", example = "a1b2c3d4-e5f67a8b9c0d-1e2f3a4b5c6d")
        UUID id,

        @Schema(description = "Store ID", example = "b1b2c3d4-e5f67a8b9-1e2f3a4b")
        UUID storeId,

        @Schema(description = "Customer identifier", example = "c1b2c3-e5f67a8b-1e2f3a4b5")
        UUID customerId,

        @Schema(description = "Unique order number", example = "ORD-2026-0001")
        String orderNumber,

        @Schema(description = "Order fulfillment type (e.g. DELIVERY, PICKUP)")
        OrderType type,

        @Schema(description = "Current processing status of the order")
        OrderStatus status,

        @Schema(description = "Customer notes or special instructions", example = "Please leave near the front door")
        String customerNote,

        @Schema(description = "Subtotal amount before fees and discounts", example = "15.00")
        BigDecimal subtotal,

        @Schema(description = "Delivery charge", example = "3.00")
        BigDecimal deliveryFee,

        @Schema(description = "Total discount applied", example = "2.00")
        BigDecimal discountTotal,

        @Schema(description = "Final total amount to pay", example = "16.00")
        BigDecimal total,

        @Schema(description = "Currency code (ISO 4217)", example = "USD")
        String currency,

        @Schema(description = "List of ordered items")
        List<OrderItemResponseDto> items
) {
}