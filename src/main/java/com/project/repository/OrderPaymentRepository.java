package com.project.repository;

import com.project.entity.Order;
import com.project.entity.OrderPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface OrderPaymentRepository extends JpaRepository<OrderPayment, UUID> {

    Optional<OrderPayment> findByOrder(Order order);

}