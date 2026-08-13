package com.project.controller;

import com.project.dto.OrderRequestDto;
import com.project.dto.OrderResponseDto;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.project.entity.OrderStatus;
import com.project.service.OrderService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import org.springframework.web.bind.annotation.ResponseStatus;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Tag(
        name = "Orders",
        description = "Order management and order tracking"
)
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "Create order",
            description = "Creates a new order. Public endpoint"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Store, product or customer not found"),
            @ApiResponse(responseCode = "409", description = "Order cannot be created"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto createOrder(
            @Valid @RequestBody OrderRequestDto request
    ) {
        return orderService.createOrder(request);
    }

    @Operation(
            summary = "Get order",
            description = "Gets an order. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{orderId}")
    public OrderResponseDto getOrderById(@PathVariable UUID orderId) {
        return orderService.getOrderById(orderId);
    }

    @Operation(
            summary = "Track order",
            description = "Tracks an order by order number and phone. Public endpoint"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/track")
    public OrderResponseDto trackOrder(
            @RequestParam String orderNumber,
            @RequestParam String phone
    ) {
        return orderService.trackOrder(orderNumber, phone);
    }

    @Operation(
            summary = "Accept order",
            description = "Accepts an order. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order accepted"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Order cannot be accepted"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{orderId}/accept")
    public OrderResponseDto acceptOrder(
            @PathVariable UUID orderId
    ) {
        return orderService.acceptOrder(orderId);
    }

    @Operation(
            summary = "Reject order",
            description = "Rejects an order. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order rejected"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Order cannot be rejected"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{orderId}/reject")
    public OrderResponseDto rejectOrder(
            @PathVariable UUID orderId,
            @RequestParam String reason
    ) {
        return orderService.rejectOrder(orderId, reason);
    }

    @Operation(
            summary = "Get new orders",
            description = "Gets new orders. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders found"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Store not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/store/{storeId}/new")
    public List<OrderResponseDto> getNewOrders(
            @PathVariable UUID storeId
    ) {
        return orderService.getNewOrders(storeId);
    }

    @Operation(
            summary = "Get store orders",
            description = "Gets store orders. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders found"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Store not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/store/{storeId}")
    public Page<OrderResponseDto> getStoreOrders(
            @PathVariable UUID storeId,
            @RequestParam(required = false) OrderStatus status,

            @Parameter(
                    description = "Example: createdAt,DESC (newest first) or createdAt,ASC (oldest first)"
            )
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt")
            Pageable pageable
    ) {
        return orderService.getStoreOrders(storeId, status, pageable);
    }

    @Operation(
            summary = "Start order",
            description = "Starts an order. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order started"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Order cannot be started"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{orderId}/start")
    public OrderResponseDto startOrder(
            @PathVariable UUID orderId
    ) {
        return orderService.startOrder(orderId);
    }

    @Operation(
            summary = "Complete order",
            description = "Completes an order. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order completed"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Order cannot be completed"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{orderId}/complete")
    public OrderResponseDto completeOrder(
            @PathVariable UUID orderId
    ) {
        return orderService.completeOrder(orderId);
    }

    @Operation(
            summary = "Cancel order",
            description = "Cancels an order. ADMIN or MERCHANT (store access)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order cancelled"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Store access denied"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Order cannot be cancelled"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{orderId}/cancel")
    public OrderResponseDto cancelOrder(
            @PathVariable UUID orderId,
            @RequestParam(required = false) String reason
    ) {
        return orderService.cancelOrder(orderId, reason);
    }
}
