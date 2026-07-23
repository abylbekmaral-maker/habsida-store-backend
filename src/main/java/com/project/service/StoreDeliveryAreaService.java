package com.project.service;

import com.project.dto.StoreDeliveryAreaRequestDto;
import com.project.dto.StoreDeliveryAreaResponseDto;
import com.project.entity.Store;
import com.project.entity.StoreDeliveryArea;
import com.project.entity.StoreDeliverySettings;
import com.project.exception.ResourceNotFoundException;
import com.project.repository.StoreDeliveryAreaRepository;
import com.project.repository.StoreDeliverySettingsRepository;
import com.project.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreDeliveryAreaService {

    private final StoreDeliveryAreaRepository areaRepository;
    private final StoreDeliverySettingsRepository settingsRepository;
    private final StoreRepository storeRepository;
    private final StoreAccessService storeAccessService;

    @Transactional
    public StoreDeliveryAreaResponseDto create(
            String storeSlug,
            StoreDeliveryAreaRequestDto request
    ) {
        StoreDeliverySettings settings = findSettingsAndCheckAccess(storeSlug);

        StoreDeliveryArea area = new StoreDeliveryArea();
        area.setDeliverySettings(settings);
        updateFields(area, request);

        return toResponse(areaRepository.save(area));
    }

    @Transactional(readOnly = true)
    public List<StoreDeliveryAreaResponseDto> getAll(String storeSlug) {

        StoreDeliverySettings settings = findSettingsAndCheckAccess(storeSlug);

        return areaRepository.findAllByDeliverySettings(settings)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public StoreDeliveryAreaResponseDto update(
            String storeSlug,
            UUID areaId,
            StoreDeliveryAreaRequestDto request
    ) {
        StoreDeliverySettings settings = findSettingsAndCheckAccess(storeSlug);

        StoreDeliveryArea area = areaRepository.findById(areaId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Delivery area not found")
                );

        if (!area.getDeliverySettings().getId().equals(settings.getId())) {
            throw new SecurityException("No access to this delivery area");
        }

        updateFields(area, request);

        return toResponse(areaRepository.save(area));
    }

    @Transactional
    public void delete(String storeSlug, UUID areaId) {

        StoreDeliverySettings settings = findSettingsAndCheckAccess(storeSlug);

        StoreDeliveryArea area = areaRepository.findById(areaId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Delivery area not found")
                );

        if (!area.getDeliverySettings().getId().equals(settings.getId())) {
            throw new SecurityException("No access to this delivery area");
        }

        areaRepository.delete(area);
    }

    private StoreDeliverySettings findSettingsAndCheckAccess(
            String storeSlug
    ) {
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

        return settingsRepository.findByStore(store)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Delivery settings not found"
                        )
                );
    }

    private void updateFields(
            StoreDeliveryArea area,
            StoreDeliveryAreaRequestDto request
    ) {
        area.setCity(request.city());
        area.setAreaName(request.areaName());
        area.setDeliveryFee(request.deliveryFee());
        area.setActive(request.active());
    }

    private StoreDeliveryAreaResponseDto toResponse(
            StoreDeliveryArea area
    ) {
        return new StoreDeliveryAreaResponseDto(
                area.getId(),
                area.getDeliverySettings().getId(),
                area.getCity(),
                area.getAreaName(),
                area.getDeliveryFee(),
                area.isActive()
        );
    }
}