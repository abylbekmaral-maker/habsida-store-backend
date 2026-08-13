package com.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record AddressResponseDto(

        @Schema(description = "Unique address identifier", example = "a9b8c7d6-1e2f3a-4b5c6")
        UUID id,

        @Schema(description = "Full street address line", example = "Street Juni, Home 4B")
        String addressLine,

        @Schema(description = "Indicates whether this is the default delivery address", example = "true")
        boolean isDefault
) {
}