package com.project.dto;

import com.project.entity.OrderType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

import java.util.UUID;

public record OrderRequestDto(

        @NotNull
        UUID storeId,

        UUID customerId,

        @NotNull
        OrderType type,

        String customerNote,

        @Valid
        @NotEmpty
        List<OrderItemRequestDto> items,

        @NotBlank(message = "Name is required")
        String firstName,

        String lastName,

        @NotBlank(message = "Phone is required")
        String phone,

        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Shipping address is required")
        String address
) {
}