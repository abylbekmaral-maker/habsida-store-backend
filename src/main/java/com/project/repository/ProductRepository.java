package com.project.repository;

import com.project.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("""
            SELECT p FROM Product p WHERE p.store.slug = :storeSlug
            AND p.store.isActive = true
            AND p.pauseOrdering = false
            AND (p.stock IS NULL OR p.stock > 0)
            AND (:categorySlug IS NULL OR p.category.slug = :categorySlug)
            """)

    Page<Product> findPublicProducts(
            @Param("storeSlug") String storeSlug,
            @Param("categorySlug") String categorySlug,
            Pageable pageable
    );

    @Query("""
        SELECT p FROM Product p
        WHERE p.store.slug = :storeSlug
          AND (:categorySlug IS NULL OR p.category.slug = :categorySlug)
    """)

    Page<Product> findProductWithFilters(
            @Param("storeSlug") String storeSlug,
            @Param("categorySlug") String categorySlug,
            Pageable pageable
    );

    boolean existsByStoreIdAndName(UUID storeId, String name);
}
