package com.project.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record AddressRequestDto(

        @NotBlank(message = "Address is required")
        String addressLine,

        boolean isDefault
) {}
