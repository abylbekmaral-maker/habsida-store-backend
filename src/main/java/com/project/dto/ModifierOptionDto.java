package com.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ModifierOptionDto (
    @NotBlank String name,
    @NotNull BigDecimal price
) {}
