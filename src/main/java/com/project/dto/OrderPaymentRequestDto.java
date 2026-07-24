package com.project.dto;

import com.project.entity.PaymentMethod;
import com.project.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request payload for updating order payment details")
public record OrderPaymentRequestDto(

        @Schema(description = "Payment method used", example = "CREDIT_CARD")
        @NotNull(message = "Payment method is required")
        PaymentMethod method,

        @Schema(description = "Current payment status", example = "PAID")
        @NotNull(message = "Payment status is required")
        PaymentStatus status,

        @Schema(description = "Payment gateway or provider name", example = "Stripe")
        String provider,

        @Schema(description = "External transaction ID from the payment provider", example = "txn_a2s3d...")
        String transactionId

) {
}
