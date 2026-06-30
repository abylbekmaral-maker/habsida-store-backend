package com.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Integer lowStockThreshold;
    private Boolean pauseOrdering;
    private Integer minQuantity;
    private Integer maxQuantity;
    private UUID storeId;
    private UUID categoryId;
}
