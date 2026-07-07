package com.project.dto;

import com.project.entity.CustomerStatus;

import java.util.List;

public record CustomerRequestDto (
    String name,
    String phone,
    CustomerStatus status,
    List<AddressDto> addresses
) {}
