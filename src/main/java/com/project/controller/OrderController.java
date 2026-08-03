package com.project.controller;

import com.project.dto.OrderRequestDto;
import com.project.dto.OrderResponseDto;
import java.util.List;

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

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto createOrder(
            @Valid @RequestBody OrderRequestDto request
    ) {
        return orderService.createOrder(request);
    }

    @GetMapping("/{orderId}")
    public OrderResponseDto getOrderById(@PathVariable UUID orderId) {
        return orderService.getOrderById(orderId);
    }

    @GetMapping("/track")
    public OrderResponseDto trackOrder(
            @RequestParam String orderNumber,
            @RequestParam String phone
    ) {
        return orderService.trackOrder(orderNumber, phone);
    }

    @PatchMapping("/{orderId}/accept")
    public OrderResponseDto acceptOrder(
            @PathVariable UUID orderId
    ) {
        return orderService.acceptOrder(orderId);
    }
    @PatchMapping("/{orderId}/reject")
    public OrderResponseDto rejectOrder(
            @PathVariable UUID orderId,
            @RequestParam String reason
    ) {
        return orderService.rejectOrder(orderId, reason);
    }
    @GetMapping("/store/{storeId}/new")
    public List<OrderResponseDto> getNewOrders(
            @PathVariable UUID storeId
    ) {
        return orderService.getNewOrders(storeId);
    }

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

    @PatchMapping("/{orderId}/start")
    public OrderResponseDto startOrder(
            @PathVariable UUID orderId
    ) {
        return orderService.startOrder(orderId);
    }
    @PatchMapping("/{orderId}/complete")
    public OrderResponseDto completeOrder(
            @PathVariable UUID orderId
    ) {
        return orderService.completeOrder(orderId);
    }

    @PatchMapping("/{orderId}/cancel")
    public OrderResponseDto cancelOrder(
            @PathVariable UUID orderId,
            @RequestParam(required = false) String reason
    ) {
        return orderService.cancelOrder(orderId, reason);
    }
}
