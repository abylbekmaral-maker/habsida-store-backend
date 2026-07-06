package com.project.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "modifier_groups")
@Getter @Setter
public class ModifierGroup {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(nullable = false)
    private String name;

    @Column(name = "is_required", nullable = false)
    private boolean isRequired;

    @Column(name = "min_select", nullable = false)
    private Integer minSelect;

    @Column(name = "max_select")
    private Integer maxSelect;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModifierOption> options = new ArrayList<>();

    @ManyToMany(mappedBy = "modifierGroups")
    private List<Product> products = new ArrayList<>();

    public void addOption(ModifierOption option) {
        options.add(option);
        option.setGroup(this);
    }
}
