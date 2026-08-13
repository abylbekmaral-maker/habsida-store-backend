package com.project.controller;

import com.project.dto.OrderPaymentRequestDto;
import com.project.dto.OrderPaymentResponseDto;
import com.project.service.OrderPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(
        name = "Payments",
        description = "Order payment management"
)
@RestController
@RequestMapping("/api/orders/{orderId}/payment")
@RequiredArgsConstructor
public class OrderPaymentController {

    private final OrderPaymentService orderPaymentService;

    @Operation(
            summary = "Get payment",
            description = "Gets order payment. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment found"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Order or payment not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public OrderPaymentResponseDto getPayment(
            @PathVariable UUID orderId
    ) {
        return orderPaymentService.getPayment(orderId);
    }

    @Operation(
            summary = "Update payment",
            description = "Updates order payment. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Order or payment not found"),
            @ApiResponse(responseCode = "409", description = "Payment cannot be updated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping
    public OrderPaymentResponseDto updatePayment(
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderPaymentRequestDto request
    ) {
        return orderPaymentService.updatePayment(orderId, request);
    }
}