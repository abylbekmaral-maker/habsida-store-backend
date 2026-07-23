package com.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse (
    @Schema(description = "Time of error occurrence", example = "2026-07-23T12:00:00")
    LocalDateTime timestamp,

    @Schema(description = "HTTP status code", example = "400")
    int status,

    @Schema(description = "Error type", example = "Bad Request")
    String error,

    @Schema(description = "Error description", example = "Input data validation error")
    String message,

    @Schema(description = "Request URI", example = "/api/v1/products")
    String path,

    @Schema(description = "Detailed list of field validation errors")
    List<FieldErrorDto> errors
    ) {
    public record FieldErrorDto(
            @Schema(description = "Field name", example = "price")
            String field,

            @Schema(description = "Cause of error", example = "The price must be greater than 0")
            String message
    ) {}
}