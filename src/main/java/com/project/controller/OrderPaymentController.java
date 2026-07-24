package com.project.controller;

import com.project.dto.OrderPaymentRequestDto;
import com.project.dto.OrderPaymentResponseDto;
import com.project.service.OrderPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders/{orderId}/payment")
@RequiredArgsConstructor
public class OrderPaymentController {

    private final OrderPaymentService orderPaymentService;

    @GetMapping
    public OrderPaymentResponseDto getPayment(
            @PathVariable UUID orderId
    ) {
        return orderPaymentService.getPayment(orderId);
    }

    @PutMapping
    public OrderPaymentResponseDto updatePayment(
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderPaymentRequestDto request
    ) {
        return orderPaymentService.updatePayment(orderId, request);
    }
}