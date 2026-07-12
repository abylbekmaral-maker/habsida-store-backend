package com.project.repository;

import com.project.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import com.project.entity.OrderStatus;
import com.project.entity.Store;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @EntityGraph(attributePaths= {"items", "items.modifiers"})
    List<Order> findAllByStoreAndStatus(Store store, OrderStatus status);
}
