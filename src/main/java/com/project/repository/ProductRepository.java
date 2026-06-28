package com.project.repository;

import com.project.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product p WHERE p.store.slug = :storeSlug " +
            "AND (:categorySlug IS NULL OR p.category.slug = :categorySlug) " +
            "AND (:pauseOrdering IS NULL OR p.pauseOrdering = :pauseOrdering)")
    List<Product> findProductWithFilters(
            @Param("storeSlug") String storeSlug,
            @Param("categorySlug") String categorySlug,
            @Param("pauseOrdering") Boolean pauseOrdering
    );

    List<Product> findByCategoryId(UUID categoryId);

    boolean existsByStoreIdAndName(UUID storeId, String name);
}
