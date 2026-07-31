package com.project.service;

import com.project.dto.OrderPaymentRequestDto;
import com.project.dto.OrderPaymentResponseDto;
import com.project.entity.Order;
import com.project.entity.OrderPayment;
import com.project.entity.PaymentStatus;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
import com.project.repository.OrderPaymentRepository;
import com.project.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderPaymentService {

    private final OrderPaymentRepository orderPaymentRepository;
    private final OrderRepository orderRepository;
    private final StoreAccessService storeAccessService;

    @Transactional(readOnly = true)
    public OrderPaymentResponseDto getPayment(UUID orderId) {

        Order order = findOrderAndCheckAccess(orderId);

        OrderPayment payment = orderPaymentRepository.findByOrder(order)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found"));

        return toResponse(payment);
    }

    @Transactional
    public OrderPaymentResponseDto updatePayment(
            UUID orderId,
            OrderPaymentRequestDto request
    ) {
        Order order = findOrderAndCheckAccess(orderId);

        OrderPayment payment = orderPaymentRepository.findByOrder(order)
                .orElseGet(() -> {
                    OrderPayment newPayment = new OrderPayment();
                    newPayment.setOrder(order);
                    newPayment.setStatus(PaymentStatus.UNPAID);
                    return newPayment;
                });

        PaymentStatus currentStatus = payment.getStatus() != null ? payment.getStatus() : PaymentStatus.UNPAID;
        PaymentStatus newStatus = request.status();

        if (currentStatus == newStatus) {
            throw new ConflictException(
                    "Payment already has status " + currentStatus
            );
        }

        boolean validTransition =
                currentStatus == PaymentStatus.UNPAID
                        && newStatus == PaymentStatus.PAID
                        || currentStatus == PaymentStatus.PAID
                        && newStatus == PaymentStatus.REFUNDED;

        if (!validTransition) {
            throw new ConflictException(
                    "Invalid payment status transition: "
                            + currentStatus + " -> " + newStatus
            );
        }
        if (payment.getId() != null) {
            if (payment.getMethod() != null && payment.getMethod() != request.method()) {
                throw new ConflictException(
                        "Payment method cannot be changed"
                );
            }

            if (payment.getProvider() != null && !Objects.equals(payment.getProvider(), request.provider()
            )) {
                throw new ConflictException(
                        "Payment provider cannot be changed"
                );
            }

            if (payment.getTransactionId() != null && !Objects.equals(payment.getTransactionId(), request.transactionId()
            )) {
                throw new ConflictException(
                        "Transaction ID cannot be changed"
                );
            }
        }

        payment.setMethod(request.method());
        payment.setStatus(request.status());
        payment.setProvider(request.provider());
        payment.setTransactionId(request.transactionId());

        if (newStatus == PaymentStatus.PAID
                && payment.getPaidAt() == null) {
            payment.setPaidAt(LocalDateTime.now());
        }

        order.setPaymentStatus(newStatus);


        return toResponse(orderPaymentRepository.save(payment));
    }

    private Order findOrderAndCheckAccess(UUID orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        if (!storeAccessService.hasStoreAccess(
                order.getStore().getSlug(),
                "ROLE_MERCHANT"
        )) {
            throw new SecurityException("No access to this store");
        }

        return order;
    }

    private OrderPaymentResponseDto toResponse(OrderPayment payment) {

        return new OrderPaymentResponseDto(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getPaidAt(),
                payment.getProvider(),
                payment.getTransactionId()
        );
    }
}
