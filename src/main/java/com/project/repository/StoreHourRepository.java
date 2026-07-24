package com.project.repository;

import com.project.entity.StoreHour;
import com.project.entity.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreHourRepository extends JpaRepository<StoreHour, UUID> {

    List<StoreHour> findAllByStoreSlugOrderByDayOfWeek(String storeSlug);

    Optional<StoreHour> findByStoreSlugAndDayOfWeek(
            String storeSlug,
            DayOfWeek dayOfWeek
    );

    boolean existsByStoreSlugAndDayOfWeek(
            String storeSlug,
            DayOfWeek dayOfWeek
    );
}