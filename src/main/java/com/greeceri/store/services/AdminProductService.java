package com.greeceri.store.services;

import com.greeceri.store.models.entity.Product;
import com.greeceri.store.models.request.AdminProductRequest;

public interface AdminProductService {
    Product createProduct(AdminProductRequest request);
    Product updateProduct(Long productId, AdminProductRequest request);
    void deleteProduct(Long productId);
}
