package com.project.dto;

import com.project.entity.DeliveryType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record StoreDeliverySettingsRequestDto(

        @NotNull
        Boolean deliveryEnabled,

        @NotNull
        DeliveryType deliveryType,

        @NotNull
        @DecimalMin("0.0")
        BigDecimal minimumOrderAmount,

        @DecimalMin("0.0")
        BigDecimal freeDeliveryThreshold,

        @DecimalMin("0.0")
        BigDecimal maxDistanceKm,

        String city,

        String zone

) {
}