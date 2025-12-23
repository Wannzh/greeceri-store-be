package com.greeceri.store.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.greeceri.store.models.entity.Product;

public interface ProductService {
    List<Product> getAllProducts();

    Page<Product> getProducts(int page, int size, Long categoryId, String keyword);

    Product getProductById(Long productId);

    List<Product> getProductsByCategoryId(Long categoryId);
}
