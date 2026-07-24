package com.project.service;

import com.project.dto.StoreDeliveryRestrictionRequestDto;
import com.project.dto.StoreDeliveryRestrictionResponseDto;
import com.project.entity.Store;
import com.project.entity.StoreDeliveryRestriction;
import com.project.entity.StoreDeliverySettings;
import com.project.exception.ResourceNotFoundException;
import com.project.repository.StoreDeliveryRestrictionRepository;
import com.project.repository.StoreDeliverySettingsRepository;
import com.project.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreDeliveryRestrictionService {

    private final StoreDeliveryRestrictionRepository restrictionRepository;
    private final StoreDeliverySettingsRepository settingsRepository;
    private final StoreRepository storeRepository;
    private final StoreAccessService storeAccessService;

    @Transactional
    public StoreDeliveryRestrictionResponseDto create(
            String storeSlug,
            StoreDeliveryRestrictionRequestDto request
    ) {
        StoreDeliverySettings settings = findSettingsAndCheckAccess(storeSlug);

        StoreDeliveryRestriction restriction =
                new StoreDeliveryRestriction();

        restriction.setDeliverySettings(settings);
        updateFields(restriction, request);

        return toResponse(restrictionRepository.save(restriction));
    }

    @Transactional(readOnly = true)
    public List<StoreDeliveryRestrictionResponseDto> getAll(
            String storeSlug
    ) {
        StoreDeliverySettings settings = findSettingsAndCheckAccess(storeSlug);

        return restrictionRepository
                .findAllByDeliverySettings(settings)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public StoreDeliveryRestrictionResponseDto update(
            String storeSlug,
            UUID restrictionId,
            StoreDeliveryRestrictionRequestDto request
    ) {
        StoreDeliverySettings settings = findSettingsAndCheckAccess(storeSlug);

        StoreDeliveryRestriction restriction =
                restrictionRepository.findById(restrictionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Delivery restriction not found"
                                )
                        );

        if (!restriction.getDeliverySettings()
                .getId()
                .equals(settings.getId())) {
            throw new SecurityException(
                    "No access to this delivery restriction"
            );
        }

        updateFields(restriction, request);

        return toResponse(restrictionRepository.save(restriction));
    }

    @Transactional
    public void delete(
            String storeSlug,
            UUID restrictionId
    ) {
        StoreDeliverySettings settings = findSettingsAndCheckAccess(storeSlug);

        StoreDeliveryRestriction restriction =
                restrictionRepository.findById(restrictionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Delivery restriction not found"
                                )
                        );

        if (!restriction.getDeliverySettings()
                .getId()
                .equals(settings.getId())) {
            throw new SecurityException(
                    "No access to this delivery restriction"
            );
        }

        restrictionRepository.delete(restriction);
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
            StoreDeliveryRestriction restriction,
            StoreDeliveryRestrictionRequestDto request
    ) {
        restriction.setRestrictionType(request.restrictionType());
        restriction.setRestrictionValue(request.restrictionValue());
        restriction.setDescription(request.description());
        restriction.setActive(request.active());
    }

    private StoreDeliveryRestrictionResponseDto toResponse(
            StoreDeliveryRestriction restriction
    ) {
        return new StoreDeliveryRestrictionResponseDto(
                restriction.getId(),
                restriction.getDeliverySettings().getId(),
                restriction.getRestrictionType(),
                restriction.getRestrictionValue(),
                restriction.getDescription(),
                restriction.isActive()
        );
    }
}