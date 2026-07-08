package com.project.dto;

import java.util.List;
import java.util.UUID;

public record ModifierGroupResponseDto(
   UUID id,
   String name,
   boolean isRequired,
   Integer minSelect,
   Integer maxSelect,
   List<ModifierOptionResponseDto> options
) {}
