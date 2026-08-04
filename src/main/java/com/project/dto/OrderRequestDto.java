package com.project.dto;

import com.project.entity.DeliveryType;
import com.project.entity.OrderType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record OrderRequestDto(

        @NotNull(message = "Store ID is required")
        UUID storeId,

        UUID customerId,

        @NotNull(message = "Order type is required")
        OrderType type,

        DeliveryType deliveryMethod,

        String customerNote,
        
        @NotBlank(message = "First name is required")
        String firstName,

        String lastName,

        @NotBlank(message = "Phone is required")
        String phone,

        @Email(message = "Invalid email format")
        String email,
        
        String recipientName,
        String recipientPhone,
        
        String deliveryAddress,
        String deliveryCity,
        String deliveryAreaName,
        String deliveryInstructions,
        UUID deliveryAreaId,

        @Valid
        @NotEmpty(message = "Order must contain at least one item")
        List<OrderItemRequestDto> items
) {
}