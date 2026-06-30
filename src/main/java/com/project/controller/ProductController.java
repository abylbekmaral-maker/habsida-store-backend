package com.project.controller;

import com.project.dto.ProductDto;
import com.project.dto.ProductResponseDto;
import com.project.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stores/{storeSlug}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts(
            @PathVariable String storeSlug,
            @RequestParam(required = false) String categorySlug,
            @RequestParam(required = false) Boolean pauseOrdering) {
     List<ProductResponseDto> products = productService.getProducts(storeSlug, categorySlug, pauseOrdering);
     return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(
            @PathVariable String storeSlug,
            @PathVariable UUID id) {
                return ResponseEntity.ok(productService.getProductById(storeSlug,id));
    }

    @PostMapping
    @PreAuthorize("@storeSecurity.hasStoreAccess(#storeSlug, 'ROLE_MERCHANT')")
    public ResponseEntity<ProductResponseDto> createProduct(
            @PathVariable String storeSlug,
            @Valid @RequestBody ProductDto request) {
        return ResponseEntity.ok(productService.createProduct(storeSlug, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@storeSecurity.hasStoreAccess(#storeSlug, 'ROLE_MERCHANT')")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable String storeSlug,
            @PathVariable UUID id,
            @Valid @RequestBody ProductDto request) {
        return ResponseEntity.ok(productService.updateProduct(storeSlug, id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@storeSecurity.hasStoreAccess(#storeSlug, 'ROLE_MERCHANT')")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable String storeSlug,
            @PathVariable UUID id) {
        productService.deleteProduct(storeSlug,id);
        return ResponseEntity.noContent().build();
    }
}
