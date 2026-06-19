package com.project.controller;

import com.project.dto.CreateMerchantRequest;
import com.project.entity.User;
import com.project.entity.UserStoreAccess;
import com.project.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/merchant")
    public User createMerchant(@Valid @RequestBody CreateMerchantRequest request) {
        return userService.createMerchant(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{userId}/stores/{storeId}")
    public UserStoreAccess assignMerchantToStore(
            @PathVariable UUID userId,
            @PathVariable UUID storeId
    ) {
        return userService.assignMerchantToStore(userId, storeId);
    }
}