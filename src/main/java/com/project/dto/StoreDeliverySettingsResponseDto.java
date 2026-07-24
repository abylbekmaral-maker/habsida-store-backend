package com.project.dto;

import com.project.entity.DeliveryType;
import java.math.BigDecimal;
import java.util.UUID;

public record StoreDeliverySettingsResponseDto(
        UUID id,
        UUID storeId,
        boolean deliveryEnabled,
        DeliveryType deliveryType,
        BigDecimal minimumOrderAmount,
        BigDecimal freeDeliveryThreshold,
        BigDecimal maxDistanceKm,
        String city,
        String zone

) {
}