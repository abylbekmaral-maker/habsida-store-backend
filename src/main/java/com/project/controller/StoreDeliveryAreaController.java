package com.project.controller;

import com.project.dto.StoreDeliveryAreaRequestDto;
import com.project.dto.StoreDeliveryAreaResponseDto;
import com.project.service.StoreDeliveryAreaService;
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
        name = "Delivery Areas",
        description = "Store delivery areas"
)
@RestController
@RequestMapping("/api/stores/{storeSlug}/delivery-areas")
@RequiredArgsConstructor
public class StoreDeliveryAreaController {

    private final StoreDeliveryAreaService areaService;

    @Operation(
            summary = "Create delivery area",
            description = "Creates a delivery area. ADMIN or MERCHANT (store access)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Delivery area created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Store not found"),
            @ApiResponse(responseCode = "409", description = "Delivery area already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public StoreDeliveryAreaResponseDto create(
            @PathVariable String storeSlug,
            @Valid @RequestBody StoreDeliveryAreaRequestDto request
    ) {
        return areaService.create(storeSlug, request);
    }

    @Operation(
            summary = "Get delivery areas",
            description = "Gets delivery areas. ADMIN or MERCHANT (store access)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delivery areas found"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Store not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public List<StoreDeliveryAreaResponseDto> getAll(
            @PathVariable String storeSlug
    ) {
        return areaService.getAll(storeSlug);
    }

    @Operation(
            summary = "Update delivery area",
            description = "Updates a delivery area. ADMIN or MERCHANT (store access)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delivery area updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Delivery area or store not found"),
            @ApiResponse(responseCode = "409", description = "Delivery area already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{areaId}")
    public StoreDeliveryAreaResponseDto update(
            @PathVariable String storeSlug,
            @PathVariable UUID areaId,
            @Valid @RequestBody StoreDeliveryAreaRequestDto request
    ) {
        return areaService.update(storeSlug, areaId, request);
    }

    @Operation(
            summary = "Delete delivery area",
            description = "Deletes a delivery area. ADMIN or MERCHANT (store access)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Delivery area deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Delivery area or store not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{areaId}")
    public void delete(
            @PathVariable String storeSlug,
            @PathVariable UUID areaId
    ) {
        areaService.delete(storeSlug, areaId);
    }
}