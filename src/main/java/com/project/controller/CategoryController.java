package com.project.controller;

import com.project.dto.CategoryDto;
import com.project.dto.CategoryResponseDto;
import com.project.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(
        name = "Categories",
        description = "Public categories and category management"
)
@RestController
@RequestMapping("/api/stores/{storeSlug}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "Get categories",
            description = "Gets store categories. Public endpoint."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories found"),
            @ApiResponse(responseCode = "404", description = "Store not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories(@PathVariable String storeSlug) {
        return ResponseEntity.ok(categoryService.getCategoriesByStore(storeSlug));
    }

    @Operation(
            summary = "Create category",
            description = "Creates a new category. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Store not found"),
            @ApiResponse(responseCode = "409", description = "Category already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("@storeSecurity.hasStoreAccess(#storeSlug, 'ROLE_MERCHANT')")
    public ResponseEntity<CategoryResponseDto> createCategory(
            @PathVariable String storeSlug,
            @Valid @RequestBody CategoryDto request) {
        CategoryResponseDto category = categoryService.createCategory(
                storeSlug,
                request.getName(),
                request.getSlug()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @Operation(
            summary = "Delete category",
            description = "Deletes a category. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Category deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Category or store not found"),
            @ApiResponse(responseCode = "409", description = "Category cannot be deleted"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("@storeSecurity.hasStoreAccess(#storeSlug, 'ROLE_MERCHANT')")
    public ResponseEntity<Void> deleteCategory(@PathVariable String storeSlug, @PathVariable UUID id) {
        categoryService.deleteCategory(storeSlug, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Update category",
            description = "Updates a category. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Category or store not found"),
            @ApiResponse(responseCode = "409", description = "Category already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    @PreAuthorize("@storeSecurity.hasStoreAccess(#storeSlug, 'ROLE_MERCHANT')")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @PathVariable String storeSlug,
            @PathVariable UUID id,
            @Valid @RequestBody CategoryDto request) {

        return ResponseEntity.ok(
                categoryService.updateCategory(storeSlug, id, request)
        );
    }
}
