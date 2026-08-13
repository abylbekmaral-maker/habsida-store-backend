package com.project.controller;

import com.project.dto.StoreDeliveryRestrictionRequestDto;
import com.project.dto.StoreDeliveryRestrictionResponseDto;
import com.project.service.StoreDeliveryRestrictionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(
        name = "Delivery Restrictions",
        description = "Store delivery restrictions"
)
@RestController
@RequestMapping("/api/stores/{storeSlug}/delivery-restrictions")
@RequiredArgsConstructor
public class StoreDeliveryRestrictionController {

    private final StoreDeliveryRestrictionService restrictionService;

    @Operation(
            summary = "Create delivery restriction",
            description = "Creates a delivery restriction. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Delivery restriction created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Store not found"),
            @ApiResponse(responseCode = "409", description = "Delivery restriction already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public StoreDeliveryRestrictionResponseDto create(
            @PathVariable String storeSlug,
            @Valid @RequestBody StoreDeliveryRestrictionRequestDto request
    ) {
        return restrictionService.create(storeSlug, request);
    }

    @Operation(
            summary = "Get delivery restrictions",
            description = "Gets delivery restrictions. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delivery restrictions found"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Store not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public List<StoreDeliveryRestrictionResponseDto> getAll(
            @PathVariable String storeSlug
    ) {
        return restrictionService.getAll(storeSlug);
    }

    @Operation(
            summary = "Update delivery restriction",
            description = "Updates a delivery restriction. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delivery restriction updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Delivery restriction or store not found"),
            @ApiResponse(responseCode = "409", description = "Delivery restriction already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{restrictionId}")
    public StoreDeliveryRestrictionResponseDto update(
            @PathVariable String storeSlug,
            @PathVariable UUID restrictionId,
            @Valid @RequestBody StoreDeliveryRestrictionRequestDto request
    ) {
        return restrictionService.update(
                storeSlug,
                restrictionId,
                request
        );
    }

    @Operation(
            summary = "Delete delivery restriction",
            description = "Deletes a delivery restriction. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Delivery restriction deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Delivery restriction or store not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{restrictionId}")
    public void delete(
            @PathVariable String storeSlug,
            @PathVariable UUID restrictionId
    ) {
        restrictionService.delete(storeSlug, restrictionId);
    }
}