package com.project.dto;

import java.util.UUID;

public record StoreDeliveryRestrictionResponseDto(
        UUID id,
        UUID deliverySettingsId,
        String restrictionType,
        String restrictionValue,
        String description,
        boolean active

) {
}