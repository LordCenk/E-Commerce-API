package com.lordcenk.ecommerceapi.service;

import com.lordcenk.ecommerceapi.dto.PagedResponse;
import com.lordcenk.ecommerceapi.dto.ProductRequest;
import com.lordcenk.ecommerceapi.dto.ProductResponse;
import com.lordcenk.ecommerceapi.exception.DuplicateResourceException;
import com.lordcenk.ecommerceapi.exception.ResourceNotFoundException;
import com.lordcenk.ecommerceapi.model.Category;
import com.lordcenk.ecommerceapi.model.Product;
import com.lordcenk.ecommerceapi.repository.CategoryRepository;
import com.lordcenk.ecommerceapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public PagedResponse<ProductResponse> getProducts(Long categoryId, String name, Pageable pageable) {
        boolean hasCategory = categoryId != null;
        boolean hasName = StringUtils.hasText(name);

        var page = hasCategory && hasName
                ? productRepository.findByCategoryIdAndNameContainingIgnoreCase(categoryId, name, pageable)
                : hasCategory
                    ? productRepository.findByCategoryId(categoryId, pageable)
                    : hasName
                        ? productRepository.findByNameContainingIgnoreCase(name, pageable)
                        : productRepository.findAll(pageable);

        return PagedResponse.from(page.map(this::toResponse));
    }

    public ProductResponse getProductById(Long id) {
        return toResponse(findProductOrThrow(id));
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = findCategoryOrThrow(request.getCategoryId());
        if (StringUtils.hasText(request.getSku()) && productRepository.existsBySkuIgnoreCase(request.getSku())) {
            throw new DuplicateResourceException("A product with SKU '" + request.getSku() + "' already exists");
        }
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .sku(request.getSku())
                .category(category)
                .build();
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = findProductOrThrow(id);
        Category category = findCategoryOrThrow(request.getCategoryId());

        if (StringUtils.hasText(request.getSku())
                && !request.getSku().equalsIgnoreCase(product.getSku())
                && productRepository.existsBySkuIgnoreCase(request.getSku())) {
            throw new DuplicateResourceException("A product with SKU '" + request.getSku() + "' already exists");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setSku(request.getSku());
        product.setCategory(category);
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = findProductOrThrow(id);
        productRepository.delete(product);
    }

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private Category findCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .sku(product.getSku())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
