package com.project.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record StoreDeliveryAreaResponseDto(
        UUID id,
        UUID deliverySettingsId,
        String city,
        String areaName,
        BigDecimal deliveryFee,
        boolean active

) {
}