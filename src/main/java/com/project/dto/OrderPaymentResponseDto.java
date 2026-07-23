package com.project.dto;

import com.project.entity.PaymentMethod;
import com.project.entity.PaymentStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record OrderPaymentResponseDto(
        UUID id,
        UUID orderId,
        PaymentMethod method,
        PaymentStatus status,
        LocalDateTime paidAt,
        String provider,
        String transactionId

) {
}