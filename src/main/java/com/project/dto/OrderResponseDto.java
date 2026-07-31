package com.project.dto;

import com.project.entity.OrderStatus;
import com.project.entity.OrderType;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

public record OrderResponseDto(
        UUID id,
        UUID storeId,
        UUID customerId,
        String orderNumber,
        OrderType type,
        OrderStatus status,
        String deliveryAddress,
        String customerNote,
        BigDecimal subtotal,
        BigDecimal deliveryFee,
        BigDecimal discountTotal,
        BigDecimal total,
        String currency,
        List<OrderItemResponseDto> items
) {
}