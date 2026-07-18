package com.project.service;

import com.project.dto.CategoryDto;
import com.project.dto.CategoryResponseDto;
import com.project.entity.Category;
import com.project.entity.Store;
import com.project.exception.ConflictException;
import com.project.exception.ResourceNotFoundException;
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
    private final StoreAccessService storeAccessService;

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getCategoriesByStore(String storeSlug) {

        Store store = storeRepository.findBySlug(storeSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        checkStoreAccess(storeSlug);

        return categoryRepository.findByStoreSlug(storeSlug)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Transactional
    public CategoryResponseDto createCategory(String storeSlug, String name, String slug) {
        checkStoreAccess(storeSlug);

        Store store = storeRepository.findBySlug(storeSlug)
                .orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        if (categoryRepository.existsByStoreIdAndName(store.getId(), name)) {
            throw new ConflictException("Category with this name already exists in this store");
        }
        if(categoryRepository.existsByStoreIdAndSlug(store.getId(), slug)) {
            throw new ConflictException("Category with this slug already exists in this store.");
        }

        Category category = new Category();
        category.setName(name);
        category.setSlug(slug);
        category.setStore(store);

        return toResponseDto(categoryRepository.save(category));
    }
    @Transactional
    public void deleteCategory(String storeSlug, UUID id) {
        checkStoreAccess(storeSlug);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getStore().getSlug().equals(storeSlug)) {
            throw new ConflictException("Category does not belong to this store");
        }

        categoryRepository.delete(category);
    }

    @Transactional
    public CategoryResponseDto updateCategory(String storeSlug, UUID id, CategoryDto dto) {
        checkStoreAccess(storeSlug);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getStore().getSlug().equals(storeSlug)) {
            throw new ConflictException("Category does not belong to this store");
        }

        if (!category.getName().equals(dto.getName())
                && categoryRepository.existsByStoreIdAndName(category.getStore().getId(), dto.getName())) {
            throw new ConflictException("Category with this name already exists in this store");
        }

        if (!category.getSlug().equals(dto.getSlug())
                && categoryRepository.existsByStoreIdAndSlug(category.getStore().getId(), dto.getSlug())) {
            throw new ConflictException("Category with this slug already exists in this store");
        }

        category.setName(dto.getName());
        category.setSlug(dto.getSlug());

        return toResponseDto(categoryRepository.save(category));
    }
    private void checkStoreAccess(String storeSlug) {
        if (!storeAccessService.hasStoreAccess(storeSlug, "ROLE_MERCHANT")) {
            throw new SecurityException("No access to this store");
        }
    }
    private CategoryResponseDto toResponseDto(Category category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getStore().getId()
        );
    }
}
