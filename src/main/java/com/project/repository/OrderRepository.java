package com.project.repository;

import com.project.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
import com.project.entity.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    Page<Order> findByStoreIdAndStatus(UUID storeId, OrderStatus status, Pageable pageable);

    Page<Order> findByStoreId(UUID storeId, Pageable pageable);

    Page<Order> findByCustomerId(UUID customerId, Pageable pageable);

    Optional<Order> findByOrderNumberAndCustomerPhone(String orderNumber, String customerPhone);

    @EntityGraph(attributePaths = {"items", "items.modifiers"})
    Optional<Order> findWithDetailsById(UUID id);
}
