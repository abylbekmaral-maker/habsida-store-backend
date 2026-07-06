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

import java.util.UUID;

@RestController
@RequestMapping("/api/stores/{storeSlug}/modifiers")
@RequiredArgsConstructor
public class ModifierController {

    private final ModifierService modifierService;

    @PostMapping
    @PreAuthorize("@storeSecurity.hasStoreAccess(#storeSlug, 'ROLE_MERCHANT')")
    public ResponseEntity<ModifierGroupResponseDto> createGroup(
            @PathVariable("storeSlug") String storeSlug,
            @Valid @RequestBody ModifierGroupDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modifierService.createModifierGroup(storeSlug, request));
    }

    @DeleteMapping("/{groupId}")
    @PreAuthorize("@storeSecurity.hasStoreAccess(#storeSlug, 'ROLE_MERCHANT')")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable("storeSlug") String storeSlug,
            @PathVariable("groupId")UUID groupId) {
        modifierService.deleteModifierGroup(storeSlug, groupId);
        return ResponseEntity.noContent().build();
    }
}
