package com.project.dto;

import java.util.UUID;

public record AddressDto(
        UUID id,
        String addressLine,
        boolean isDefault
) {}
