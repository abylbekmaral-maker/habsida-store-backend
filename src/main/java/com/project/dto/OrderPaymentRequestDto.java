package com.project.dto;

import com.project.entity.PaymentMethod;
import com.project.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;

public record OrderPaymentRequestDto(

        @NotNull
        PaymentMethod method,

        @NotNull
        PaymentStatus status,

        String provider,

        String transactionId

) {
}