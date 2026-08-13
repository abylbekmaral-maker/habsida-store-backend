package com.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateStoreRequest {

    @NotBlank(message = "Store name is required")
    private String name;

    @NotBlank(message = "Store slug is required")
    private String slug;

    @NotNull(message = "Owner id is required")
    private UUID ownerId;
}