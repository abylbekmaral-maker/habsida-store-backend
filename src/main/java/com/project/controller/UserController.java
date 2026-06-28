package com.project.controller;

import com.project.dto.CreateMerchantRequest;
import com.project.dto.UserResponseDto;
import com.project.entity.User;
import com.project.entity.UserStoreAccess;
import com.project.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public UserResponseDto createMerchant(@Valid @RequestBody CreateMerchantRequest request) {
        User user = userService.createMerchant(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        UserResponseDto response = new UserResponseDto();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());

        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{userId}/stores/{storeId}")
    public ResponseEntity<Void> assignMerchantToStore(
            @PathVariable UUID userId,
            @PathVariable UUID storeId
    ) {
        userService.assignMerchantToStore(userId, storeId);
        return ResponseEntity.ok().build();
    }
}