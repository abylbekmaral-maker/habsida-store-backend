package com.project.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import jakarta.validation.Valid;
import java.util.List;

public record OrderItemRequestDto(

        @NotNull
        UUID productId,

        @NotNull
        Integer quantity,

        @Valid
        List<OrderItemModifierRequestDto> modifiers
) {
}