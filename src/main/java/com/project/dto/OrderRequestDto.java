package com.project.dto;

import com.project.entity.OrderType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

import java.util.UUID;

public record OrderRequestDto(

        @NotNull
        UUID storeId,

        @NotNull
        UUID customerId,

        @NotNull
        OrderType type,

        String customerNote,

        @Valid
        @NotEmpty
        List<OrderItemRequestDto> items
) {
}