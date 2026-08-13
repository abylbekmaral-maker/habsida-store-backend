package com.project.service;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class OrderTotalCalculatorTest {

    private final OrderTotalCalculator calculator = new OrderTotalCalculator();

    @Test
    void shouldCalculateTotal() {

        BigDecimal total = calculator.calculate(
                new BigDecimal("10000"),
                new BigDecimal("2000"),
                new BigDecimal("1000")
        );

        assertEquals(new BigDecimal("11000"), total);
    }

    @Test
    void shouldThrowExceptionWhenTotalIsNegative() {

        assertThrows(IllegalArgumentException.class, () ->
                calculator.calculate(
                        new BigDecimal("1000"),
                        BigDecimal.ZERO,
                        new BigDecimal("2000")
                )
        );
    }
}