package com.project.dto;

import java.util.UUID;

public record ProductImageDto (
  UUID id,
  String imageUrl,
  Integer sortOrder
) {}
