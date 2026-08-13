package com.project.controller;

import com.project.dto.StoreDeliverySettingsRequestDto;
import com.project.dto.StoreDeliverySettingsResponseDto;
import com.project.service.StoreDeliverySettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(
        name = "Delivery Settings",
        description = "Store delivery settings"
)
@RestController
@RequestMapping("/api/stores/{storeSlug}/delivery-settings")
@RequiredArgsConstructor
public class StoreDeliverySettingsController {

    private final StoreDeliverySettingsService settingsService;

    @Operation(
            summary = "Create delivery settings",
            description = "Creates delivery settings. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Delivery settings created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Store not found"),
            @ApiResponse(responseCode = "409", description = "Delivery settings already exist"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public StoreDeliverySettingsResponseDto create(
            @PathVariable String storeSlug,
            @Valid @RequestBody StoreDeliverySettingsRequestDto request
    ) {
        return settingsService.create(storeSlug, request);
    }

    @Operation(
            summary = "Get delivery settings",
            description = "Gets delivery settings. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delivery settings found"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Store or delivery settings not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public StoreDeliverySettingsResponseDto get(
            @PathVariable String storeSlug
    ) {
        return settingsService.get(storeSlug);
    }

    @Operation(
            summary = "Update delivery settings",
            description = "Updates delivery settings. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delivery settings updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Store or delivery settings not found"),
            @ApiResponse(responseCode = "409", description = "Delivery settings conflict"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping
    public StoreDeliverySettingsResponseDto update(
            @PathVariable String storeSlug,
            @Valid @RequestBody StoreDeliverySettingsRequestDto request
    ) {
        return settingsService.update(storeSlug, request);
    }

    @Operation(
            summary = "Delete delivery settings",
            description = "Deletes delivery settings. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Delivery settings deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Store or delivery settings not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void delete(
            @PathVariable String storeSlug
    ) {
        settingsService.delete(storeSlug);
    }
}