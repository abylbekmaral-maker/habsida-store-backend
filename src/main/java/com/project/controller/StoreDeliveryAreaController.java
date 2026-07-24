package com.project.controller;

import com.project.dto.StoreDeliveryAreaRequestDto;
import com.project.dto.StoreDeliveryAreaResponseDto;
import com.project.service.StoreDeliveryAreaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stores/{storeSlug}/delivery-areas")
@RequiredArgsConstructor
public class StoreDeliveryAreaController {

    private final StoreDeliveryAreaService areaService;

    @PostMapping
    public StoreDeliveryAreaResponseDto create(
            @PathVariable String storeSlug,
            @Valid @RequestBody StoreDeliveryAreaRequestDto request
    ) {
        return areaService.create(storeSlug, request);
    }

    @GetMapping
    public List<StoreDeliveryAreaResponseDto> getAll(
            @PathVariable String storeSlug
    ) {
        return areaService.getAll(storeSlug);
    }

    @PutMapping("/{areaId}")
    public StoreDeliveryAreaResponseDto update(
            @PathVariable String storeSlug,
            @PathVariable UUID areaId,
            @Valid @RequestBody StoreDeliveryAreaRequestDto request
    ) {
        return areaService.update(storeSlug, areaId, request);
    }

    @DeleteMapping("/{areaId}")
    public void delete(
            @PathVariable String storeSlug,
            @PathVariable UUID areaId
    ) {
        areaService.delete(storeSlug, areaId);
    }
}