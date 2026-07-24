package com.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "store_delivery_areas")
@Getter
@Setter
public class StoreDeliveryArea extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_settings_id", nullable = false)
    private StoreDeliverySettings deliverySettings;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(name = "area_name", nullable = false, length = 150)
    private String areaName;

    @Column(name = "delivery_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    @Column(nullable = false)
    private boolean active = true;
}