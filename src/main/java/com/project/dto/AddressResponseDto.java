package com.project.dto;

import java.util.UUID;

public record AddressResponseDto(
        UUID id,
        String addressLine,
        boolean isDefault
) {
}