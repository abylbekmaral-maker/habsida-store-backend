package com.project.service;

import com.project.dto.ProductDto;
import com.project.entity.Category;
import com.project.entity.Product;
import com.project.entity.Store;
import com.project.repository.CategoryRepository;
import com.project.repository.ProductRepository;
import com.project.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;

    @Transactional(readOnly = true)
    public List<Product> getProducts(String storeSlug, String categorySlug, Boolean pauseOrdering) {
        return productRepository.findProductWithFilters(storeSlug, categorySlug, pauseOrdering);
    }

    @Transactional(readOnly = true)
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Transactional
    public Product createProduct(String storeSlug, ProductDto dto) {
        Store store = storeRepository.findBySlug(storeSlug)
                .orElseThrow(() -> new RuntimeException("Store not found"));

        Category category = null;
        if(dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            if(!category.getStore().getId().equals(store.getId())) {
                throw new RuntimeException("Selected category belongs to another store");
            }
        }

        if (productRepository.existsByStoreIdAndName(store.getId(), dto.getName())) {
            throw new RuntimeException("Product with this name already exists in this store");
        }
        Product product = new Product();
        product.setStore(store);
        product.setCategory(category);
        updateProductFields(product, dto);

        return productRepository.save(product);
    }

    @Transactional
    public Product createProduct(Store store, Category category, String name, BigDecimal price) {
        if (store == null) {
            throw new IllegalArgumentException("Product must be linked to a store");
        }
        if (category != null) {
            if (!category.getStore().getId().equals(store.getId())) {
                throw new IllegalArgumentException("Selected category belongs to another store");
            }
        }

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setStore(store);
        product.setCategory(category);

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(UUID id, ProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getName().equals(dto.getName()) &&
        productRepository.existsByStoreIdAndName(product.getStore().getId(), dto.getName())) {
            throw new RuntimeException("Product with this name already exists in this store");
        }

        if(dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            if(!category.getStore().getId().equals(product.getStore().getId())) {
                throw new RuntimeException("Selected category belongs to another store");
            }
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }
        updateProductFields(product, dto);
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        if(!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    private void updateProductFields(Product product, ProductDto dto) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setLowStockThreshold(dto.getLowStockThreshold() != null ? dto.getLowStockThreshold(): 5);
        product.setPauseOrdering(dto.isPauseOrdering());
        product.setMinQuantity(dto.getMinQuantity() != null ? dto.getMinQuantity() : 1);
        product.setMaxQuantity(dto.getMaxQuantity());
    }
}
