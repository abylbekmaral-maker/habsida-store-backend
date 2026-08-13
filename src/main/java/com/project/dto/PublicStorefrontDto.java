package com.project.dto;

import java.util.List;
import java.util.UUID;

public record PublicStorefrontDto(

        UUID id,
        String name,
        String slug,
        StoreDeliverySettingsResponseDto checkoutSettings,
        List<StoreHourResponseDto> hours,
        List<StoreDeliveryAreaResponseDto> deliveryAreas
) {
}