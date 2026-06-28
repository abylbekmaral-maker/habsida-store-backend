package com.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryDto {

    @NotBlank(message = "Category name is required")
    @Size(max = 255, message = "Category name must be less than 255 characters")
    private String name;

    @NotBlank(message = "Category slug is required")
    @Size(max = 255, message = "Category slug must be less than 255 characters")
    private String slug;
}