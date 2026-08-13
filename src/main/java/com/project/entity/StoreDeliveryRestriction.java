package com.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "store_delivery_restrictions")
@Getter
@Setter
public class StoreDeliveryRestriction extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_settings_id", nullable = false)
    private StoreDeliverySettings deliverySettings;

    @Column(name = "restriction_type", nullable = false, length = 50)
    private String restrictionType;

    @Column(name = "restriction_value", length = 255)
    private String restrictionValue;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private boolean active = true;
}