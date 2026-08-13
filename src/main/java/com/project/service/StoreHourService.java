package com.project.service;

import com.project.dto.StoreBreakRequestDto;
import com.project.dto.StoreBreakResponseDto;
import com.project.dto.StoreHourRequestDto;
import com.project.dto.StoreHourResponseDto;
import com.project.entity.Store;
import com.project.entity.StoreBreak;
import com.project.entity.StoreHour;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
import com.project.repository.StoreHourRepository;
import com.project.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreHourService {

    private final StoreHourRepository storeHourRepository;
    private final StoreRepository storeRepository;
    private final StoreAccessService storeAccessService;

    @Transactional
    public StoreHourResponseDto create(
            String storeSlug,
            StoreHourRequestDto request
    ) {
        Store store = findStoreAndCheckAccess(storeSlug);

        if (storeHourRepository.existsByStoreSlugAndDayOfWeek(
                storeSlug,
                request.dayOfWeek()
        )) {
            throw new ConflictException(
                    "Schedule for this day already exists"
            );
        }

        validateRequest(request);

        StoreHour storeHour = new StoreHour();
        storeHour.setStore(store);
        storeHour.setDayOfWeek(request.dayOfWeek());

        applyRequest(storeHour, request);

        return toResponse(storeHourRepository.save(storeHour));
    }

    @Transactional(readOnly = true)
    public List<StoreHourResponseDto> getAll(String storeSlug) {

        findStoreAndCheckAccess(storeSlug);

        return storeHourRepository
                .findAllByStoreSlugOrderByDayOfWeek(storeSlug)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public StoreHourResponseDto update(
            String storeSlug,
            UUID id,
            StoreHourRequestDto request
    ) {
        findStoreAndCheckAccess(storeSlug);

        StoreHour storeHour = findStoreHour(id, storeSlug);

        if (storeHour.getDayOfWeek() != request.dayOfWeek()
                && storeHourRepository.existsByStoreSlugAndDayOfWeek(
                storeSlug,
                request.dayOfWeek()
        )) {
            throw new ConflictException(
                    "Schedule for this day already exists"
            );
        }

        validateRequest(request);

        storeHour.setDayOfWeek(request.dayOfWeek());
        applyRequest(storeHour, request);

        return toResponse(storeHourRepository.save(storeHour));
    }

    @Transactional
    public void delete(String storeSlug, UUID id) {

        findStoreAndCheckAccess(storeSlug);

        StoreHour storeHour = findStoreHour(id, storeSlug);

        storeHourRepository.delete(storeHour);
    }

    private void applyRequest(
            StoreHour storeHour,
            StoreHourRequestDto request
    ) {
        storeHour.setClosed(request.closed());

        if (request.closed()) {
            storeHour.setOpenTime(null);
            storeHour.setCloseTime(null);
            storeHour.setLastOrderCutoffTime(null);
            storeHour.getBreaks().clear();
            return;
        }

        storeHour.setOpenTime(request.openTime());
        storeHour.setCloseTime(request.closeTime());
        storeHour.setLastOrderCutoffTime(
                request.lastOrderCutoffTime()
        );

        storeHour.getBreaks().clear();

        if (request.breaks() != null) {
            for (StoreBreakRequestDto breakRequest : request.breaks()) {

                StoreBreak storeBreak = new StoreBreak();
                storeBreak.setStoreHour(storeHour);
                storeBreak.setStartTime(breakRequest.startTime());
                storeBreak.setEndTime(breakRequest.endTime());

                storeHour.getBreaks().add(storeBreak);
            }
        }
    }

    private void validateRequest(StoreHourRequestDto request) {

        if (request.closed()) {
            return;
        }

        if (request.openTime() == null
                || request.closeTime() == null) {
            throw new ConflictException(
                    "Open time and close time are required"
            );
        }

        if (!request.openTime().isBefore(request.closeTime())) {
            throw new ConflictException(
                    "Open time must be before close time"
            );
        }

        if (request.lastOrderCutoffTime() != null
                && (request.lastOrderCutoffTime()
                .isBefore(request.openTime())
                || request.lastOrderCutoffTime()
                .isAfter(request.closeTime()))) {
            throw new ConflictException(
                    "Last order cutoff time must be within working hours"
            );
        }

        if (request.breaks() != null) {
            for (StoreBreakRequestDto storeBreak : request.breaks()) {

                if (!storeBreak.startTime()
                        .isBefore(storeBreak.endTime())) {
                    throw new ConflictException(
                            "Break start time must be before end time"
                    );
                }

                if (storeBreak.startTime()
                        .isBefore(request.openTime())
                        || storeBreak.endTime()
                        .isAfter(request.closeTime())) {
                    throw new ConflictException(
                            "Break must be within working hours"
                    );
                }
            }
        }
    }

    private StoreHour findStoreHour(
            UUID id,
            String storeSlug
    ) {
        StoreHour storeHour = storeHourRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Store schedule not found"
                        ));

        if (!storeHour.getStore().getSlug().equals(storeSlug)) {
            throw new ResourceNotFoundException(
                    "Store schedule not found"
            );
        }

        return storeHour;
    }

    private Store findStoreAndCheckAccess(String storeSlug) {

        Store store = storeRepository.findBySlug(storeSlug)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Store not found"
                        ));

        if (!storeAccessService.hasStoreAccess(
                storeSlug,
                "ROLE_MERCHANT"
        )) {
            throw new SecurityException(
                    "No access to this store"
            );
        }

        return store;
    }

    private StoreHourResponseDto toResponse(StoreHour storeHour) {

        List<StoreBreakResponseDto> breaks =
                storeHour.getBreaks()
                        .stream()
                        .map(storeBreak ->
                                new StoreBreakResponseDto(
                                        storeBreak.getId(),
                                        storeBreak.getStartTime(),
                                        storeBreak.getEndTime()
                                ))
                        .toList();

        return new StoreHourResponseDto(
                storeHour.getId(),
                storeHour.getDayOfWeek(),
                storeHour.getOpenTime(),
                storeHour.getCloseTime(),
                storeHour.isClosed(),
                storeHour.getLastOrderCutoffTime(),
                breaks
        );
    }
}