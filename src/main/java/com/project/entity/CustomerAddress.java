package com.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "customer_addresses")
@Getter @Setter
public class CustomerAddress {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "address_line", nullable = false)
    private String addressLine;

    @Column(name = "is_default")
    private boolean isDefault;
}
