package com.project.dto;

import com.project.entity.CustomerStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Customer profile response payload")
public record CustomerResponseDto(

        @Schema(description = "Unique customer identifier", example = "c1b2c3d4-e5f67a8b-9c0d1e")
        UUID id,

        @Schema(description = "Customer full name", example = "Walter White")
        String name,

        @Schema(description = "Customer phone number", example = "+984567890")
        String phone,

        @Schema(description = "Current account status of the customer")
        CustomerStatus status,

        @Schema(description = "List of associated delivery addresses")
        List<AddressResponseDto> addresses
) {}
