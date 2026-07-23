package com.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductResponseDto (
        @Schema(description = "Unique product ID", example = "d29c35f6-3dcf-4e10-ab67-cfd8aac1cf40")
        UUID id,

        @Schema(description = "Product name", example = "Antonovka Apple")
        String name,

        @Schema(description = "Detailed product description", example = "Fresh juicy apples")
        String description,

        @Schema(description = "Base price of the product", example = "5.50")
        BigDecimal price,

        @Schema(description = "Current stock quantity", example = "500")
        Integer stock,

        @Schema(description = "Low stock alert threshold", example = "20")
        Integer lowStockThreshold,

        @Schema(description = "Flag indicating whether ordering is temporarily paused", example = "false")
        Boolean pauseOrdering,

        @Schema(description = "Minimum allowed quantity per order", example = "1")
        Integer minQuantity,

        @Schema(description = "Maximum allowed quantity per order", example = "50")
        Integer maxQuantity,

        @Schema(description = "Store ID", example = "b1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
        UUID storeId,

        @Schema(description = "Category ID", example = "c1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d")
        UUID categoryId,

        @Schema(description = "List of product images")
        List<ProductImageDto> images,

        @Schema(description = "List of modifier groups attached to this product")
        List<ModifierGroupResponseDto> modifierGroups
) {}