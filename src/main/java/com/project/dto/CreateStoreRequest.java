package com.project.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateStoreRequest {

    @NotBlank(message = "Store name is required")
    private String name;

    @NotBlank(message = "Store slug is required")
    private String slug;
}
