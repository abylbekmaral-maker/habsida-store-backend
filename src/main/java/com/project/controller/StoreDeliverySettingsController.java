package com.project.controller;

import com.project.dto.StoreDeliverySettingsRequestDto;
import com.project.dto.StoreDeliverySettingsResponseDto;
import com.project.service.StoreDeliverySettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stores/{storeSlug}/delivery-settings")
@RequiredArgsConstructor
public class StoreDeliverySettingsController {

    private final StoreDeliverySettingsService settingsService;

    @PostMapping
    public StoreDeliverySettingsResponseDto create(
            @PathVariable String storeSlug,
            @Valid @RequestBody StoreDeliverySettingsRequestDto request
    ) {
        return settingsService.create(storeSlug, request);
    }

    @GetMapping
    public StoreDeliverySettingsResponseDto get(
            @PathVariable String storeSlug
    ) {
        return settingsService.get(storeSlug);
    }

    @PutMapping
    public StoreDeliverySettingsResponseDto update(
            @PathVariable String storeSlug,
            @Valid @RequestBody StoreDeliverySettingsRequestDto request
    ) {
        return settingsService.update(storeSlug, request);
    }

    @DeleteMapping
    public void delete(
            @PathVariable String storeSlug
    ) {
        settingsService.delete(storeSlug);
    }
}