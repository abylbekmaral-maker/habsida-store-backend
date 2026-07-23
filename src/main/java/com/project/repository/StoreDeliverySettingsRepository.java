package com.project.repository;

import com.project.entity.Store;
import com.project.entity.StoreDeliverySettings;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface StoreDeliverySettingsRepository
        extends JpaRepository<StoreDeliverySettings, UUID> {

    Optional<StoreDeliverySettings> findByStore(Store store);
}