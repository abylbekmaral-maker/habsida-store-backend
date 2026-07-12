package com.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "order_item_modifiers")
@Getter
@Setter
public class OrderItemModifier extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modifier_option_id", nullable = false)
    private ModifierOption modifierOption;

    @Column(name = "modifier_name_snapshot", nullable = false)
    private String modifierNameSnapshot;

    @Column(name = "modifier_price_snapshot", nullable = false)
    private BigDecimal modifierPriceSnapshot;
}