package com.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories", uniqueConstraints = {
        @UniqueConstraint(name = "uk_store_category_name", columnNames = {"store_id", "name"}),
        @UniqueConstraint(name = "uk_store_category_slug", columnNames = {"store_id", "slug"})
})
@Getter
@Setter
public class Category extends BaseEntity{

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;
}
