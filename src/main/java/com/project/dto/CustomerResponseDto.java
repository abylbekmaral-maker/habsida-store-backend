package com.project.dto;

import com.project.entity.CustomerStatus;

import java.util.List;
import java.util.UUID;

public record CustomerResponseDto(
        UUID id,
        String name,
        String phone,
        CustomerStatus status,
        List<AddressDto> addresses
) {}
