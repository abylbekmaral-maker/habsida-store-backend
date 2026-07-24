package com.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Category details response payload")
public record CategoryResponseDto (

    @Schema(description = "Unique category ID",example = "4d8gx0a2s1-d4k1a3a6e-d3g4f2s3a")
    UUID id,

    @Schema(description = "Category name",example = "Apples")
    String name,

    @Schema(description = "URL-Friendly category slug",example = "apples")
    String slug,

    @Schema(description = "Associated store identifier",example = "z1x2c3v4-b5n6m7-6b7v5c4x3a2")
    UUID storeId
) {}
