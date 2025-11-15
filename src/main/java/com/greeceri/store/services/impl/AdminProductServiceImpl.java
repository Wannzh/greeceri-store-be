package com.greeceri.store.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.greeceri.store.models.entity.Category;
import com.greeceri.store.models.entity.Product;
import com.greeceri.store.models.request.AdminProductRequest;
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
}
