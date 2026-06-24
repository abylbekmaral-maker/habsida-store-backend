package com.project.service;

import com.project.entity.Category;
import com.project.entity.Product;
import com.project.entity.Store;
import com.project.repository.CategoryRepository;
import com.project.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

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
}
