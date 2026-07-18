package com.project.controller;

import com.project.dto.OrderRequestDto;
import com.project.dto.OrderResponseDto;
import java.util.List;
import com.project.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
}
