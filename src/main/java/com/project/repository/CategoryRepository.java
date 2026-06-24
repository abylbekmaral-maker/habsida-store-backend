package com.project.repository;


import com.project.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByStoreSlug(String storeSlug);

    Optional<Category> findByStoreSlugAndSlug(String storeSlug, String slug);

    boolean existsByStoreIdAndName(UUID storeId, String name);

    boolean existsByStoreIdAndSlug(UUID storeId, String slug);
}
