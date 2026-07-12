package com.project.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressDto(

        @NotBlank(message = "Address is required")
        String addressLine,

        boolean isDefault
) {}
