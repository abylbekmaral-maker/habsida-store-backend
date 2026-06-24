package com.project.repository;

import com.project.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findByStoreSlug(String storeSlug);

    List<Product> findByCategoryId(UUID categoryId);

    List<Product> findByStoreSlugAndCategorySlug(String storeSlug, String categorySlug);

    boolean existsByStoreIdAndName(UUID storeId, String name);
}
