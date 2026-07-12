package com.project.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class OrderTotalCalculator {

    public BigDecimal calculate(
            BigDecimal subtotal,
            BigDecimal deliveryFee,
            BigDecimal discountTotal
    ) {
        BigDecimal safeSubtotal = Objects.requireNonNullElse(subtotal, BigDecimal.ZERO);
        BigDecimal safeDeliveryFee = Objects.requireNonNullElse(deliveryFee, BigDecimal.ZERO);
        BigDecimal safeDiscountTotal = Objects.requireNonNullElse(discountTotal, BigDecimal.ZERO);

        if (safeSubtotal.signum() < 0
                || safeDeliveryFee.signum() < 0
                || safeDiscountTotal.signum() < 0) {
            throw new IllegalArgumentException("Invalid order amount");
        }

        BigDecimal total = safeSubtotal
                .add(safeDeliveryFee)
                .subtract(safeDiscountTotal);

        if (total.signum() < 0) {
            throw new IllegalArgumentException("Total order amount cannot be negative. Discount is too high");
        }

        return total;
    }
}
