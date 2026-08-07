package com.project.controller;

import com.project.dto.CreateStoreRequest;
import com.project.dto.PublicStorefrontDto;
import com.project.dto.StoreResponseDto;
import com.project.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Tag(
        name = "Stores",
        description = "Store management and public storefront endpoints"
)
public class StoreController {

    private final StoreService storeService;

    @Operation(
            summary = "Get public storefront",
            description = "Returns public store information. Public endpoint"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Store found"),
            @ApiResponse(responseCode = "404", description = "Store not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{storeSlug}/storefront")
    public PublicStorefrontDto getPublicStorefront(
            @PathVariable String storeSlug
    ) {
        return storeService.getPublicStorefront(storeSlug);
    }

    @Operation(
            summary = "Create store",
            description = "Creates a new store. ADMIN only."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Store created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "ADMIN only"),
            @ApiResponse(responseCode = "404", description = "Owner not found"),
            @ApiResponse(responseCode = "409", description = "Store already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoreResponseDto createStore(@Valid @RequestBody CreateStoreRequest request) {
        return storeService.createStore(request);
    }

    @Operation(
            summary = "Get all stores",
            description = "Returns all stores. ADMIN only."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stores found"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "ADMIN only"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<StoreResponseDto> getAllStores() {
        return storeService.getAllStores();
    }

    @Operation(
            summary = "Get my store",
            description = "Returns the merchant's store. MERCHANT only."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Store found"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Store not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('MERCHANT')")
    @GetMapping("/{storeId}/my")
    public StoreResponseDto getMyStore(
            @PathVariable UUID storeId,
            Authentication authentication
    ) {
        return storeService.getStoreForMerchant(storeId, authentication.getName());
    }

}
