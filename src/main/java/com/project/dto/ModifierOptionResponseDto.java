package com.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Modifier option response payload")
public record ModifierOptionResponseDto(

        @Schema(description = "Unique modifier option ID", example = "m1b2c3d4-e5f69c0d-14b5c6d")
        UUID id,

        @Schema(description = "Modifier option name", example = "Box")
        String name,

        @Schema(description = "Additional price for selecting this option", example = "0.50")
        BigDecimal price
) {}
