package com.project.controller;

import com.project.dto.CategoryDto;
import com.project.entity.Category;
import com.project.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stores/{storeSlug}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories(@PathVariable String storeSlug) {
        return ResponseEntity.ok(categoryService.getCategoriesByStore(storeSlug));
    }

    @PostMapping
    @PreAuthorize("@storeSecurity.hasStoreAccess(#storeSlug, 'ROLE_MERCHANT')")
    public ResponseEntity<Category> createCategory(
            @PathVariable String storeSlug,
            @Valid @RequestBody CategoryDto request) {
        Category category = categoryService.createCategory(
                storeSlug,
                request.getName(),
                request.getSlug()
        );

        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@storeSecurity.hasStoreAccess(#storeSlug, 'ROLE_MERCHANT')")
    public ResponseEntity<Void> deleteCategory(@PathVariable String storeSlug, @PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
