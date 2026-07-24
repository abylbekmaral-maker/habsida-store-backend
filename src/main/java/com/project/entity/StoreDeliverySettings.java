package com.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "store_delivery_settings")
@Getter
@Setter
public class StoreDeliverySettings extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false, unique = true)
    private Store store;

    @Column(name = "delivery_enabled", nullable = false)
    private boolean deliveryEnabled = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type", nullable = false, length = 30)
    private DeliveryType deliveryType;

    @Column(name = "minimum_order_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal minimumOrderAmount = BigDecimal.ZERO;

    @Column(name = "free_delivery_threshold", precision = 15, scale = 2)
    private BigDecimal freeDeliveryThreshold;

    @Column(name = "max_distance_km", precision = 8, scale = 2)
    private BigDecimal maxDistanceKm;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String zone;

    @OneToMany(
            mappedBy = "deliverySettings",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<StoreDeliveryArea> areas = new ArrayList<>();

    @OneToMany(
            mappedBy = "deliverySettings",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<StoreDeliveryRestriction> restrictions = new ArrayList<>();
}