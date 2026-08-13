package com.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StoreDeliveryRestrictionRequestDto(

        @NotBlank
        String restrictionType,

        String restrictionValue,

        String description,

        @NotNull
        Boolean active

) {
}