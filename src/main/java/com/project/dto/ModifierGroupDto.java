package com.project.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ModifierGroupDto (
        @NotBlank(message = "Modifier group name is required")
        String name,

        boolean isRequired,

        int minSelect,

        Integer maxSelect,

        @Valid
        List<ModifierOptionDto> options
) {}
