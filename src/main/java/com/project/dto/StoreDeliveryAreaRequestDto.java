package com.project.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record StoreDeliveryAreaRequestDto(

        @NotBlank
        String city,

        @NotBlank
        String areaName,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal deliveryFee,

        @NotNull
        Boolean active

) {
}