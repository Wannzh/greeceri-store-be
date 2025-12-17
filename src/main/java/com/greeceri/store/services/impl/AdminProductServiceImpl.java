package com.greeceri.store.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.greeceri.store.models.entity.Category;
import com.greeceri.store.models.entity.Product;
import com.greeceri.store.models.request.AdminProductRequest;
import com.greeceri.store.models.response.AdminProductSummaryResponse;
import com.greeceri.store.repositories.CategoryRepository;
import com.greeceri.store.repositories.ProductRepository;
import com.greeceri.store.services.AdminProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {
        private final ProductRepository productRepository;
        private final CategoryRepository categoryRepository;

        @Override
        public Page<AdminProductSummaryResponse> getProducts(int page, int size, String keyword) {
                if (keyword != null && keyword.trim().isEmpty()) {
                        keyword = null;
                }

                Pageable pageable = PageRequest.of(page, size);
                Page<Product> productPage = productRepository.findAllByKeyword(keyword, pageable);

                return productPage.map(this::mapToSummaryResponse);
        }

        @Override
        @Transactional
        public Product createProduct(AdminProductRequest request) {
                Category category = categoryRepository.findById(request.getCategoryId())
                                .orElseThrow(() -> new RuntimeException("Category not found"));

                Product newProduct = Product.builder()
                                .name(request.getName())
                                .description(request.getDescription())
                                .price(request.getPrice())
                                .stock(request.getStock())
                                .imageUrl(request.getImageUrl())
                                .category(category)
                                .build();

                return productRepository.save(newProduct);
        }

        @Override
        @Transactional
        public Product updateProduct(Long productId, AdminProductRequest request) {
                Product productToUpdate = productRepository.findById(productId)
                                .orElseThrow(() -> new RuntimeException("Product not found!"));

                Category category = categoryRepository.findById(request.getCategoryId())
                                .orElseThrow(() -> new RuntimeException("Category not found"));

                // Update Field
                productToUpdate.setName(request.getName());
                productToUpdate.setDescription(request.getDescription());
                productToUpdate.setPrice(request.getPrice());
                productToUpdate.setStock(request.getStock());
                productToUpdate.setImageUrl(request.getImageUrl());
                productToUpdate.setCategory(category);

                return productRepository.save(productToUpdate);
        }

        @Override
        @Transactional
        public void deleteProduct(Long productId) {
                Product productToDelete = productRepository.findById(productId)
                                .orElseThrow(() -> new RuntimeException("Product not found!"));

                productRepository.delete(productToDelete);
        }

        private AdminProductSummaryResponse mapToSummaryResponse(Product product) {
                return AdminProductSummaryResponse.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .description(product.getDescription())
                                .price(product.getPrice())
                                .stock(product.getStock())
                                .imageUrl(product.getImageUrl())
                                .categoryName(product.getCategory().getName())
                                .categoryId(product.getCategory().getId())
                                .build();
        }
}
