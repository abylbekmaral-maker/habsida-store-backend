package com.project.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record OrderItemModifierRequestDto(

        @NotNull
        UUID modifierOptionId
) {
}