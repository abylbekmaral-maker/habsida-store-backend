package com.project.dto;

import com.project.entity.PaymentMethod;
import com.project.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response payload containing order payment details")
public record OrderPaymentResponseDto(

        @Schema(description = "Unique payment identifier", example = "p1b2c3d4-f68b-9c0d1e2f3")
        UUID id,

        @Schema(description = "Associated order identifier", example = "a1b2c3d4-zc2x21-e5c6d")
        UUID orderId,

        @Schema(description = "Payment method used", example = "CREDIT_CARD")
        PaymentMethod method,

        @Schema(description = "Current payment status", example = "PAID")
        PaymentStatus status,

        @Schema(description = "Timestamp when the payment was completed", example = "2026-07-23T20:00:23")
        LocalDateTime paidAt,

        @Schema(description = "Payment gateway or provider name", example = "Stripe")
        String provider,

        @Schema(description = "External transaction ID from the payment provider", example = "txn_a1asz.")
        String transactionId

) {
}
