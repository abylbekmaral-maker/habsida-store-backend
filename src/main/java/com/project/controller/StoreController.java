package com.project.controller;

import com.project.dto.CreateStoreRequest;
import com.project.entity.Store;
import com.project.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Store createStore(@Valid @RequestBody CreateStoreRequest request) {
        return storeService.createStore(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Store> getAllStores() {
        return storeService.getAllStores();
    }

    @PreAuthorize("hasRole('MERCHANT')")
    @GetMapping("/{storeId}/my")
    public Store getMyStore(
            @PathVariable UUID storeId,
            Authentication authentication
    ) {
        return storeService.getStoreForMerchant(storeId, authentication.getName());
    }
}
