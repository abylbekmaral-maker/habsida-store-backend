package com.project.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductResponseDto (
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        Integer lowStockThreshold,
        Boolean pauseOrdering,
        Integer minQuantity,
        Integer maxQuantity,
        UUID storeId,
        UUID categoryId,
        List<ProductImageDto> images,
        List<ModifierGroupResponseDto> modifierGroups
) {}