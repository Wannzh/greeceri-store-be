package com.greeceri.store.services;

import org.springframework.data.domain.Page;

import com.greeceri.store.models.request.AdminProductRequest;
import com.greeceri.store.models.response.AdminProductSummaryResponse;

public interface AdminProductService {
    Page<AdminProductSummaryResponse> getProducts(int page, int size, String keyword);

    AdminProductSummaryResponse createProduct(AdminProductRequest request);

    AdminProductSummaryResponse updateProduct(Long productId, AdminProductRequest request);

    void deleteProduct(Long productId);
}
