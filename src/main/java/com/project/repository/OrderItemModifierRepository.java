package com.project.repository;

import com.project.entity.OrderItemModifier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OrderItemModifierRepository extends JpaRepository<OrderItemModifier, UUID> {
}