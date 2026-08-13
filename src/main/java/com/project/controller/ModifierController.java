package com.project.controller;

import com.project.dto.ModifierGroupDto;
import com.project.dto.ModifierGroupResponseDto;
import com.project.service.ModifierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(
        name = "Modifiers",
        description = "Public modifiers and modifier management"
)
@RestController
@RequestMapping("/api/stores/{storeSlug}/modifiers")
@RequiredArgsConstructor
public class ModifierController {

    private final ModifierService modifierService;

    @Operation(
            summary = "Get modifier groups",
            description = "Gets modifier groups. Public endpoint"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Modifier groups found"),
            @ApiResponse(responseCode = "404", description = "Store not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<ModifierGroupResponseDto>> getGroups(
            @PathVariable String storeSlug) {
        return ResponseEntity.ok(modifierService.getModifierGroupsByStore(storeSlug));
    }

    @Operation(
            summary = "Get modifier group",
            description = "Gets a modifier group. Public endpoint"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Modifier group found"),
            @ApiResponse(responseCode = "404", description = "Modifier group or store not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{groupId}")
    public ResponseEntity<ModifierGroupResponseDto> getGroup(
            @PathVariable String storeSlug,
            @PathVariable UUID groupId) {
        return ResponseEntity.ok(modifierService.getModifierGroupById(storeSlug, groupId));
    }

    @Operation(
            summary = "Create modifier group",
            description = "Creates a modifier group. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Modifier group created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Store not found"),
            @ApiResponse(responseCode = "409", description = "Modifier group already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    @PreAuthorize("@storeSecurity.hasStoreAccess(#storeSlug, 'ROLE_MERCHANT')")
    public ResponseEntity<ModifierGroupResponseDto> createGroup(
            @PathVariable("storeSlug") String storeSlug,
            @Valid @RequestBody ModifierGroupDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modifierService.createModifierGroup(storeSlug, request));
    }

    @Operation(
            summary = "Update modifier group",
            description = "Updates a modifier group. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Modifier group updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Modifier group or store not found"),
            @ApiResponse(responseCode = "409", description = "Modifier group already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{groupId}")
    @PreAuthorize("@storeSecurity.hasStoreAccess(#storeSlug, 'ROLE_MERCHANT')")
    public ResponseEntity<ModifierGroupResponseDto> updateGroup(
            @PathVariable String storeSlug,
            @PathVariable UUID groupId,
            @Valid @RequestBody ModifierGroupDto request) {
        return ResponseEntity.ok(modifierService.updateModifierGroup(storeSlug, groupId, request));
    }

    @Operation(
            summary = "Delete modifier group",
            description = "Deletes a modifier group. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Modifier group deleted"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Modifier group or store not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{groupId}")
    @PreAuthorize("@storeSecurity.hasStoreAccess(#storeSlug, 'ROLE_MERCHANT')")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable("storeSlug") String storeSlug,
            @PathVariable("groupId") UUID groupId) {
        modifierService.deleteModifierGroup(storeSlug, groupId);
        return ResponseEntity.noContent().build();
    }
}
