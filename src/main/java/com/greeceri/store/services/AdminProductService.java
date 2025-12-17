package com.greeceri.store.services;

import org.springframework.data.domain.Page;

import com.greeceri.store.models.entity.Product;
import com.greeceri.store.models.request.AdminProductRequest;
import com.greeceri.store.models.response.AdminProductSummaryResponse;

public interface AdminProductService {
    Page<AdminProductSummaryResponse> getProducts(int page, int size, String keyword);

    Product createProduct(AdminProductRequest request);

    Product updateProduct(Long productId, AdminProductRequest request);

    void deleteProduct(Long productId);
}
