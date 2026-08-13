package com.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Modifier group response payload")
public record ModifierGroupResponseDto(

        @Schema(description = "Unique modifier group ID", example = "mg123456-e5f67a8b-9c0d13d")
        UUID id,

        @Schema(description = "Modifier group name", example = "Type packing")
        String name,

        @Schema(description = "Specifies if selecting an option from this group is required", example = "true")
        boolean isRequired,

        @Schema(description = "Minimum number of options that must be selected", example = "1")
        Integer minSelect,

        @Schema(description = "Maximum number of options that can be selected", example = "1")
        Integer maxSelect,

        @Schema(description = "List of options in this modifier group")
        List<ModifierOptionResponseDto> options
) {}
