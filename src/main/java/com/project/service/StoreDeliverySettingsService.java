package com.project.service;

import com.project.dto.StoreDeliverySettingsRequestDto;
import com.project.dto.StoreDeliverySettingsResponseDto;
import com.project.entity.Store;
import com.project.entity.StoreDeliverySettings;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
import com.project.repository.StoreDeliverySettingsRepository;
import com.project.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreDeliverySettingsService {

    private final StoreDeliverySettingsRepository settingsRepository;
    private final StoreRepository storeRepository;
    private final StoreAccessService storeAccessService;

    @Transactional
    public StoreDeliverySettingsResponseDto create(
            String storeSlug,
            StoreDeliverySettingsRequestDto request
    ) {
        Store store = findStoreAndCheckAccess(storeSlug);

        if (settingsRepository.findByStore(store).isPresent()) {
            throw new ConflictException(
                    "Delivery settings already exist for this store"
            );
        }

        StoreDeliverySettings settings = new StoreDeliverySettings();
        settings.setStore(store);

        updateFields(settings, request);

        return toResponse(settingsRepository.save(settings));
    }

    @Transactional(readOnly = true)
    public StoreDeliverySettingsResponseDto get(String storeSlug) {

        Store store = findStoreAndCheckAccess(storeSlug);

        StoreDeliverySettings settings = settingsRepository.findByStore(store)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Delivery settings not found"
                        )
                );

        return toResponse(settings);
    }

    @Transactional
    public StoreDeliverySettingsResponseDto update(
            String storeSlug,
            StoreDeliverySettingsRequestDto request
    ) {
        Store store = findStoreAndCheckAccess(storeSlug);

        StoreDeliverySettings settings = settingsRepository.findByStore(store)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Delivery settings not found"
                        )
                );

        updateFields(settings, request);

        return toResponse(settingsRepository.save(settings));
    }

    @Transactional
    public void delete(String storeSlug) {

        Store store = findStoreAndCheckAccess(storeSlug);

        StoreDeliverySettings settings = settingsRepository.findByStore(store)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Delivery settings not found"
                        )
                );

        settingsRepository.delete(settings);
    }

    private Store findStoreAndCheckAccess(String storeSlug) {

        Store store = storeRepository.findBySlug(storeSlug)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Store not found")
                );

        if (!storeAccessService.hasStoreAccess(
                storeSlug,
                "ROLE_MERCHANT"
        )) {
            throw new SecurityException("No access to this store");
        }

        return store;
    }

    private void updateFields(
            StoreDeliverySettings settings,
            StoreDeliverySettingsRequestDto request
    ) {
        settings.setDeliveryEnabled(request.deliveryEnabled());
        settings.setDeliveryType(request.deliveryType());
        settings.setMinimumOrderAmount(request.minimumOrderAmount());
        settings.setFreeDeliveryThreshold(request.freeDeliveryThreshold());
        settings.setMaxDistanceKm(request.maxDistanceKm());
        settings.setCity(request.city());
        settings.setZone(request.zone());
    }

    private StoreDeliverySettingsResponseDto toResponse(
            StoreDeliverySettings settings
    ) {
        return new StoreDeliverySettingsResponseDto(
                settings.getId(),
                settings.getStore().getId(),
                settings.isDeliveryEnabled(),
                settings.getDeliveryType(),
                settings.getMinimumOrderAmount(),
                settings.getFreeDeliveryThreshold(),
                settings.getMaxDistanceKm(),
                settings.getCity(),
                settings.getZone()
        );
    }
}