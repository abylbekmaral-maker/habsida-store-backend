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

@RestController
@RequestMapping("/api/stores/{storeSlug}/hours")
@RequiredArgsConstructor
public class StoreHourController {

    private final StoreHourService storeHourService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoreHourResponseDto create(
            @PathVariable String storeSlug,
            @Valid @RequestBody StoreHourRequestDto request
    ) {
        return storeHourService.create(storeSlug, request);
    }

    @GetMapping
    public List<StoreHourResponseDto> getAll(
            @PathVariable String storeSlug
    ) {
        return storeHourService.getAll(storeSlug);
    }

    @PutMapping("/{id}")
    public StoreHourResponseDto update(
            @PathVariable String storeSlug,
            @PathVariable UUID id,
            @Valid @RequestBody StoreHourRequestDto request
    ) {
        return storeHourService.update(storeSlug, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable String storeSlug,
            @PathVariable UUID id
    ) {
        storeHourService.delete(storeSlug, id);
    }
}