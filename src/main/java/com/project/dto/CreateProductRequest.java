package com.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Request to create a new product")
public record CreateProductRequest (
        @Schema(description = "Product Name", example = "Antonovka Apple")
        @NotBlank(message = "Product name cannot be empty")
        @Size(max = 255, message = "The title must not exceed 255 characters")
        String name,

        @Schema(description = "Detailed product description", example = "Fresh, juicy apples from our own garden")
        String description,

        @Schema(description = "Product price", example = "5.50")
        @NotNull(message = "Enter a price")
        @Positive(message = "Price must be greater than 0")
        BigDecimal price,

        @Schema(description = "Quantity in stock", example = "500")
        @Min(value = 0, message = "The stock cannot be negative")
        Integer stock,

        @Schema(description = "Category ID", example = "b1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
        @NotNull(message = "Category is required")
        UUID categoryId
) {}
