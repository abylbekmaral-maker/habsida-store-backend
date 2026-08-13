package com.project.repository;

import com.project.entity.StoreDeliveryArea;
import com.project.entity.StoreDeliverySettings;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface StoreDeliveryAreaRepository
        extends JpaRepository<StoreDeliveryArea, UUID> {

    List<StoreDeliveryArea> findAllByDeliverySettings(
            StoreDeliverySettings deliverySettings
    );
}