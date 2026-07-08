package com.project.dto;

import com.project.entity.CustomerStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CustomerRequestDto (
        @NotBlank(message = "Customer name is required")
        String name,

        @NotBlank(message = "Phone is required")
        String phone,

        CustomerStatus status,

        @Valid
        List<AddressDto> addresses
) {}
