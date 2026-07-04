package com.project.service;

import com.project.dto.ProductDto;
import com.project.dto.ProductImageDto;
import com.project.dto.ProductResponseDto;
import com.project.entity.Category;
import com.project.entity.Product;
import com.project.entity.ProductImage;
import com.project.entity.Store;
import com.project.repository.CategoryRepository;
import com.project.repository.ProductRepository;
import com.project.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final StoreRepository storeRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProducts(String storeSlug, String categorySlug, Boolean pauseOrdering) {
        return productRepository.findProductWithFilters(storeSlug, categorySlug, pauseOrdering).stream().map(this::toResponseDto).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(String storeSlug, UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.getStore().getSlug().equals(storeSlug)) {
            throw new IllegalArgumentException("Product does not belong to this store");
        }

        return toResponseDto(product);
    }

    @Transactional
    public ProductResponseDto createProduct(String storeSlug, ProductDto dto) {
        Store store = storeRepository.findBySlug(storeSlug)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        Category category = null;
        if(dto.getCategoryId() != null) {
            category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));
            if(!category.getStore().getId().equals(store.getId())) {
                throw new IllegalArgumentException("Selected category belongs to another store");
            }
        }

        if (productRepository.existsByStoreIdAndName(store.getId(), dto.getName())) {
            throw new IllegalArgumentException("Product with this name already exists in this store");
        }
        Product product = new Product();
        product.setStore(store);
        product.setCategory(category);
        updateProductFields(product, dto);

        return toResponseDto(productRepository.save(product));
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
    public ProductResponseDto updateProduct(String storeSlug, UUID id, ProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.getStore().getSlug().equals(storeSlug)) {
            throw new IllegalArgumentException("Product does not belong to this store");
        }

        if (!product.getName().equals(dto.getName()) &&
                productRepository.existsByStoreIdAndName(product.getStore().getId(), dto.getName())) {
            throw new IllegalArgumentException("Product with this name already exists in this store");
        }

        if(dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));

            if(!category.getStore().getId().equals(product.getStore().getId())) {
                throw new IllegalArgumentException("Selected category belongs to another store");
            }
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }

        updateProductFields(product, dto);
        return toResponseDto(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(String storeSlug, UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.getStore().getSlug().equals(storeSlug)) {
            throw new IllegalArgumentException("Product does not belong to this store");
        }

        productRepository.delete(product);
    }

    private void updateProductFields(Product product, ProductDto dto)
    {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setLowStockThreshold(dto.getLowStockThreshold() != null ? dto.getLowStockThreshold(): 5);
        product.setPauseOrdering(dto.isPauseOrdering());
        product.setMinQuantity(dto.getMinQuantity() != null ? dto.getMinQuantity() : 1);
        product.setMaxQuantity(dto.getMaxQuantity());
    }

    @Transactional
    public ProductResponseDto uploadImages(String storeSlug, UUID productId, List<MultipartFile> files) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (!product.getStore().getSlug().equals(storeSlug)) {
            throw new IllegalArgumentException("Product does not belong to this store");
        }

        int currentMaxOrder = product.getImages().stream()
                .mapToInt(ProductImage::getSortOrder)
                .max()
                .orElse(-1);

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String imageUrl = fileStorageService.storeFile(file);

                ProductImage image = new ProductImage();
                image.setImageUrl(imageUrl);
                image.setSortOrder(++currentMaxOrder);

                product.addImage(image);
            }
        }
        return toResponseDto(productRepository.save(product));
    }

    private ProductResponseDto toResponseDto(Product product) {
        List<ProductImageDto> imageDtos = product.getImages().stream()
                .map(image -> new ProductImageDto(
                        image.getId(),
                        image.getImageUrl(),
                        image.getSortOrder()
                ))
                .toList();

        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getLowStockThreshold(),
                product.isPauseOrdering(),
                product.getMinQuantity(),
                product.getMaxQuantity(),
                product.getStore().getId(),
                product.getCategory() != null ? product.getCategory().getId() : null,
                imageDtos
        );
    }
}
