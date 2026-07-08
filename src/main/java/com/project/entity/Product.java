package com.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products", uniqueConstraints =  {
        @UniqueConstraint(name = "uk_product_store_name", columnNames = {"store_id", "name"})
})
@Getter @Setter
public class Product extends BaseEntity{

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock = 0;

    @Column(name = "low_stock_threshold", nullable = false)
    private Integer lowStockThreshold = 5;

    @Column(name = "pause_ordering", nullable = false)
    private boolean pauseOrdering = false;

    @Column(name = "min_quantity", nullable = false)
    private Integer minQuantity = 1;

    @Column(name = "max_quantity", nullable = false)
    private Integer maxQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<ProductImage> images = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "product_modifier_groups",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "modifier_group_id")
    )
    private List<ModifierGroup> modifierGroups = new ArrayList<>();

    public void addModifierGroup(ModifierGroup group) {
        this.modifierGroups.add(group);
        group.getProducts().add(this);
    }

    public void removeModifierGroup(ModifierGroup group) {
        this.modifierGroups.remove(group);
        group.getProducts().remove(this);
    }

    public void addImage(ProductImage image) {
        images.add(image);
        image.setProduct(this);
    }
}
