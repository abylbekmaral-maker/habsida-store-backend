package com.project.controller;

import com.project.dto.StoreDeliveryRestrictionRequestDto;
import com.project.dto.StoreDeliveryRestrictionResponseDto;
import com.project.service.StoreDeliveryRestrictionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stores/{storeSlug}/delivery-restrictions")
@RequiredArgsConstructor
public class StoreDeliveryRestrictionController {

    private final StoreDeliveryRestrictionService restrictionService;

    @PostMapping
    public StoreDeliveryRestrictionResponseDto create(
            @PathVariable String storeSlug,
            @Valid @RequestBody StoreDeliveryRestrictionRequestDto request
    ) {
        return restrictionService.create(storeSlug, request);
    }

    @GetMapping
    public List<StoreDeliveryRestrictionResponseDto> getAll(
            @PathVariable String storeSlug
    ) {
        return restrictionService.getAll(storeSlug);
    }

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

    @DeleteMapping("/{restrictionId}")
    public void delete(
            @PathVariable String storeSlug,
            @PathVariable UUID restrictionId
    ) {
        restrictionService.delete(storeSlug, restrictionId);
    }
}