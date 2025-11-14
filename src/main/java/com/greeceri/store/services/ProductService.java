package com.greeceri.store.services;

import java.util.List;

import com.greeceri.store.models.entity.Product;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long productId);
    List<Product> getProductsByCategoryId(Long categoryId);
}  
