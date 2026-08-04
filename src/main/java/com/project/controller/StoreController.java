package com.project.controller;

import com.project.dto.CreateStoreRequest;
import com.project.dto.PublicStorefrontDto;
import com.project.dto.StoreResponseDto;
import com.project.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/{storeSlug}/storefront")
    public PublicStorefrontDto getPublicStorefront(
            @PathVariable String storeSlug
    ) {
        return storeService.getPublicStorefront(storeSlug);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StoreResponseDto createStore(@Valid @RequestBody CreateStoreRequest request) {
        return storeService.createStore(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<StoreResponseDto> getAllStores() {
        return storeService.getAllStores();
    }

    @PreAuthorize("hasRole('MERCHANT')")
    @GetMapping("/{storeId}/my")
    public StoreResponseDto getMyStore(
            @PathVariable UUID storeId,
            Authentication authentication
    ) {
        return storeService.getStoreForMerchant(storeId, authentication.getName());
    }
}
