package com.project.controller;

import com.project.dto.ProductDto;
import com.project.dto.ProductResponseDto;
import com.project.service.ModifierService;
import com.project.service.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.UUID;

@Tag(
        name = "Products",
        description = "Public product catalog and merchant product management"
)
@RestController
@RequestMapping("/api/stores/{storeSlug}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ModifierService modifierService;

    @Operation(
            summary = "Get public products",
            description = "Returns public products. Public endpoint"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products found"),
            @ApiResponse(responseCode = "400", description = "Invalid page parameters"),
            @ApiResponse(responseCode = "404", description = "Store or category not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getPublicProducts(
            @PathVariable String storeSlug,
            @RequestParam(required = false) String categorySlug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<ProductResponseDto> products = productService.getPublicProducts(storeSlug, categorySlug, pageable);
        return ResponseEntity.ok(products);
    }

    @Operation(
            summary = "Get merchant products",
            description = "Returns all products for the merchant's store."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products found"),
            @ApiResponse(responseCode = "400", description = "Invalid page parameters"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Store or category not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/manage")
    @PreAuthorize("@storeSecurity.hasStoreAccess(#storeSlug, 'ROLE_MERCHANT')")
    public ResponseEntity<Page<ProductResponseDto>> getMerchantProducts(
            @PathVariable String storeSlug,
            @RequestParam(required = false) String categorySlug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponseDto> products = productService.getProducts(storeSlug, categorySlug, pageable);
        return ResponseEntity.ok(products);
    }

    @Operation(
            summary = "Get product",
            description = "Gets product by ID. Public endpoint."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product or store not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(
            @PathVariable String storeSlug,
            @PathVariable UUID id) {
                return ResponseEntity.ok(productService.getProductById(storeSlug,id));
    }

    @Operation(
            summary = "Create product",
            description = "Creates a new product. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Store or category not found"),
            @ApiResponse(responseCode = "409", description = "Product already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("@storeSecurity.hasStoreAccess(#storeSlug, 'ROLE_MERCHANT')")
    public ResponseEntity<ProductResponseDto> createProduct(
            @PathVariable String storeSlug,
            @Valid @RequestBody ProductDto request) {
        return ResponseEntity.status(201)
                .body(productService.createProduct(storeSlug, request));    }

    @Operation(
            summary = "Update product",
            description = "Updates a product. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Product, store or category not found"),
            @ApiResponse(responseCode = "409", description = "Product conflict"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    @PreAuthorize("@storeSecurity.hasStoreAccess(#storeSlug, 'ROLE_MERCHANT')")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable String storeSlug,
            @PathVariable UUID id,
            @Valid @RequestBody ProductDto request) {
        return ResponseEntity.ok(productService.updateProduct(storeSlug, id, request));
    }

    @Operation(
            summary = "Delete product",
            description = "Deletes a product. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Product or store not found"),
            @ApiResponse(responseCode = "409", description = "Product cannot be deleted"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @PreAuthorize("@storeSecurity.hasStoreAccess(#storeSlug, 'ROLE_MERCHANT')")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable String storeSlug,
            @PathVariable UUID id) {
        productService.deleteProduct(storeSlug,id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Upload product images",
            description = "Uploads images for a product. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Images uploaded"),
            @ApiResponse(responseCode = "400", description = "Invalid files"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Product or store not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(value = "/{id}/images", consumes = "multipart/form-data")
    @PreAuthorize("@storeSecurity.hasStoreAccess(#storeSlug, 'ROLE_MERCHANT')")
    public ResponseEntity<ProductResponseDto> uploadProductImages(
            @PathVariable String storeSlug,
            @PathVariable UUID id,
            @RequestParam("files") List<MultipartFile> files) {
     return ResponseEntity.ok(productService.uploadImages(storeSlug, id, files));
    }

    @Operation(
            summary = "Add modifier group",
            description = "Adds a modifier group to a product. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Modifier group added"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Product, modifier group or store not found"),
            @ApiResponse(responseCode = "409", description = "Modifier group already added"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{productId}/modifiers/{groupId}")
    @PreAuthorize("@storeSecurity.hasStoreAccess(#storeSlug, 'ROLE_MERCHANT')")
    public ResponseEntity<Void> linkModifierToProduct(
            @PathVariable("storeSlug") String storeSlug,
            @PathVariable("productId") UUID productId,
            @PathVariable("groupId") UUID groupId) {
        modifierService.linkModifierGroupToProduct(storeSlug, productId, groupId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Remove modifier group",
            description = "Removes a modifier group from a product. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Modifier group removed"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Product, modifier group or store not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{productId}/modifiers/{groupId}")
    @PreAuthorize("@storeSecurity.hasStoreAccess(#storeSlug, 'ROLE_MERCHANT')")
    public ResponseEntity<Void> unlinkModifierFromProduct(
            @PathVariable("storeSlug") String storeSlug,
            @PathVariable("productId") UUID productId,
            @PathVariable("groupId") UUID groupId) {

        modifierService.unlinkModifierGroupFromProduct(storeSlug, productId, groupId);
        return ResponseEntity.noContent().build();
    }
}
