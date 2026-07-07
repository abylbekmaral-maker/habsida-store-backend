package com.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter @Setter
public class Customer {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatus status = CustomerStatus.ACTIVE;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerAddress> addresses = new ArrayList<>();

    public void addAddress(CustomerAddress address) {
        addresses.add(address);
        address.setCustomer(this);
    }
    public void removeAddress(CustomerAddress address) {
        addresses.remove(address);
        address.setCustomer(null);
    }
}
