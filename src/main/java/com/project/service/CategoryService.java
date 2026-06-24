package com.project.service;

import com.project.entity.Category;
import com.project.entity.Store;
import com.project.repository.CategoryRepository;
import com.project.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;

    @Transactional(readOnly = true)
    public List<Category> getCategoriesByStore(String storeSlug) {
        return categoryRepository.findByStoreSlug(storeSlug);
    }

    @Transactional
    public Category createCategory(String storeSlug, String name, String slug) {
        Store store = storeRepository.findBySlug(storeSlug)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        if (categoryRepository.existsByStoreIdAndName(store.getId(), name)) {
            throw new IllegalArgumentException("Category with this name already exists in this store");
        }
        if(categoryRepository.existsByStoreIdAndSlug(store.getId(), slug)) {
            throw new IllegalArgumentException("Category with this slug already exists in this store.");
        }

        Category category = new Category();
        category.setName(name);
        category.setSlug(slug);
        category.setStore(store);

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }
}
