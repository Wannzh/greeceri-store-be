package com.greeceri.store.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.greeceri.store.models.entity.Product;
import com.greeceri.store.repositories.ProductRepository;
import com.greeceri.store.services.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategory(categoryId);
    }
}
