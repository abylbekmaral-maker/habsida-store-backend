package com.project.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ModifierGroupDto (
        @NotBlank String name,
        boolean isRequired,
        int minSelect,
        Integer maxSelect,
        List<ModifierOptionDto> options
) {}
