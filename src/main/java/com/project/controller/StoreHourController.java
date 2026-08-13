package com.project.controller;

import com.project.dto.StoreHourRequestDto;
import com.project.dto.StoreHourResponseDto;
import com.project.service.StoreHourService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(
        name = "Store Hours",
        description = "Store working hours"
)
@RestController
@RequestMapping("/api/stores/{storeSlug}/hours")
@RequiredArgsConstructor
public class StoreHourController {

    private final StoreHourService storeHourService;

    @Operation(
            summary = "Create store hours",
            description = "Creates store hours. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Store hours created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Store not found"),
            @ApiResponse(responseCode = "409", description = "Store hours already exist"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoreHourResponseDto create(
            @PathVariable String storeSlug,
            @Valid @RequestBody StoreHourRequestDto request
    ) {
        return storeHourService.create(storeSlug, request);
    }

    @Operation(
            summary = "Get store hours",
            description = "Gets store hours. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Store hours found"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Store not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public List<StoreHourResponseDto> getAll(
            @PathVariable String storeSlug
    ) {
        return storeHourService.getAll(storeSlug);
    }

    @Operation(
            summary = "Update store hours",
            description = "Updates store hours. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Store hours updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Store or store hours not found"),
            @ApiResponse(responseCode = "409", description = "Store hours conflict"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public StoreHourResponseDto update(
            @PathVariable String storeSlug,
            @PathVariable UUID id,
            @Valid @RequestBody StoreHourRequestDto request
    ) {
        return storeHourService.update(storeSlug, id, request);
    }

    @Operation(
            summary = "Delete store hours",
            description = "Deletes store hours. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Store hours deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Store or store hours not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable String storeSlug,
            @PathVariable UUID id
    ) {
        storeHourService.delete(storeSlug, id);
    }
}