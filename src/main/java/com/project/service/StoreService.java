package com.project.service;

import com.project.dto.*;
import com.project.entity.Store;
import com.project.entity.StoreDeliveryArea;
import com.project.entity.StoreDeliverySettings;
import com.project.entity.User;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
import com.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final StoreAccessService storeAccessService;
    private final UserRepository userRepository;
    private final StoreDeliverySettingsRepository storeDeliverySettingsRepository;
    private final StoreDeliveryAreaRepository storeDeliveryAreaRepository;
    private final StoreHourRepository storeHourRepository;

    @Transactional
    public StoreResponseDto createStore(CreateStoreRequest request) {
        if (storeRepository.existsBySlug(request.getSlug())) {
            throw new ConflictException("Store with this slug already exists");
        }
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        Store store = new Store();
        store.setName(request.getName());
        store.setSlug(request.getSlug());
        store.setOwner(owner);

        return toResponseDto(storeRepository.save(store));
    }

    @Transactional(readOnly = true)
    public List<StoreResponseDto> getAllStores() {
        return storeRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public StoreResponseDto getStoreForMerchant(UUID storeId, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        boolean isMerchant = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_MERCHANT"));

        if (!isMerchant) {
            throw new SecurityException(
                    "This endpoint is available only for merchants"
            );
        }

        if (!storeAccessService.hasAccess(user, store)) {
            throw new SecurityException("No access to this store");
        }

        return toResponseDto(store);
    }
    @Transactional(readOnly = true)
    public PublicStorefrontDto getPublicStorefront(String storeSlug) {

        Store store = storeRepository.findBySlug(storeSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        StoreDeliverySettings settings = storeDeliverySettingsRepository
                .findByStore(store)
                .orElse(null);

        StoreDeliverySettingsResponseDto settingsDto = null;
        List<StoreDeliveryAreaResponseDto> areaDtos = List.of();

        if (settings != null) {
            settingsDto = new StoreDeliverySettingsResponseDto(
                    settings.getId(),
                    store.getId(),
                    settings.isDeliveryEnabled(),
                    settings.getDeliveryType(),
                    settings.getMinimumOrderAmount(),
                    settings.getFreeDeliveryThreshold(),
                    settings.getMaxDistanceKm(),
                    settings.getCity(),
                    settings.getZone()
            );

            areaDtos = storeDeliveryAreaRepository
                    .findAllByDeliverySettings(settings)
                    .stream()
                    .filter(StoreDeliveryArea::isActive)
                    .map(area -> new StoreDeliveryAreaResponseDto(
                            area.getId(),
                            settings.getId(),
                            area.getCity(),
                            area.getAreaName(),
                            area.getDeliveryFee(),
                            area.isActive()
                    ))
                    .toList();
        }

        List<StoreHourResponseDto> hourDtos = storeHourRepository
                .findAllByStoreSlugOrderByDayOfWeek(storeSlug)
                .stream()
                .map(hour -> new StoreHourResponseDto(
                        hour.getId(),
                        hour.getDayOfWeek(),
                        hour.getOpenTime(),
                        hour.getCloseTime(),
                        hour.isClosed(),
                        hour.getLastOrderCutoffTime(),
                        hour.getBreaks().stream()
                                .map(storeBreak -> new StoreBreakResponseDto(
                                        storeBreak.getId(),
                                        storeBreak.getStartTime(),
                                        storeBreak.getEndTime()
                                ))
                                .toList()
                ))
                .toList();

        return new PublicStorefrontDto(
                store.getId(),
                store.getName(),
                store.getSlug(),
                settingsDto,
                hourDtos,
                areaDtos
        );
    }

    private StoreResponseDto toResponseDto(Store store) {
        return new StoreResponseDto(
                store.getId(),
                store.getName(),
                store.getSlug(),
                store.getOwner().getId()
        );
    }
}
