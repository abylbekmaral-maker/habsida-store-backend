package com.project.controller;

import com.project.dto.CreateMerchantRequest;
import com.project.dto.UserResponseDto;
import com.project.entity.User;
import com.project.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(
        name = "Users",
        description = "User and merchant management"
)
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Create merchant",
            description = "Creates a merchant. ADMIN only."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Merchant created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "ADMIN only"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.CREATED)
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

    @Operation(
            summary = "Assign merchant to store",
            description = "Assigns a merchant to a store. ADMIN only."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Merchant assigned to store"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "ADMIN only"),
            @ApiResponse(responseCode = "404", description = "User or store not found"),
            @ApiResponse(responseCode = "409", description = "Merchant already assigned"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{userId}/stores/{storeId}")
    public ResponseEntity<Void> assignMerchantToStore(
            @PathVariable UUID userId,
            @PathVariable UUID storeId
    ) {
        userService.assignMerchantToStore(userId, storeId);
        return ResponseEntity.noContent().build();
    }
}