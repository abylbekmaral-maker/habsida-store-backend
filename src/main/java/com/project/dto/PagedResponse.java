package com.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

@Schema(description = "Wrapper for paginated lists")
public record PagedResponse<T> (
        @Schema(description = "List of elements on the current page")
        List<T> content,

        @Schema(description = "Current page number (starting from 0)", example = "0")
        int page,

        @Schema(description = "Number of elements per page", example = "20")
        int size,

        @Schema(description = "Total elements in the database", example = "105")
        long totalElements,

        @Schema(description = "Total pages", example = "6")
        int totalPages,

        @Schema(description = "Last page flag", example = "false")
        boolean last
) {
    public static <T> PagedResponse<T> from(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
