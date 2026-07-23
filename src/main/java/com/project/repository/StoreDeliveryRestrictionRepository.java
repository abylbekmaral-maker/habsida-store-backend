package com.project.repository;

import com.project.entity.StoreDeliveryRestriction;
import com.project.entity.StoreDeliverySettings;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface StoreDeliveryRestrictionRepository
        extends JpaRepository<StoreDeliveryRestriction, UUID> {

    List<StoreDeliveryRestriction> findAllByDeliverySettings(
            StoreDeliverySettings deliverySettings
    );
}