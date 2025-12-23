package com.greeceri.store.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.greeceri.store.models.entity.Product;
import com.greeceri.store.models.response.PublicBestSellerResponse;
import com.greeceri.store.repositories.OrderItemRepository;
import com.greeceri.store.repositories.ProductRepository;
import com.greeceri.store.services.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Page<Product> getProducts(int page, int size, Long categoryId, String keyword) {
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAllWithFilter(categoryId, keyword, pageable);
    }

    @Override
    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<PublicBestSellerResponse> getBestSellersThisWeek(int limit) {
        List<Object[]> results = orderItemRepository.findBestSellersThisWeek(limit);
        List<PublicBestSellerResponse> bestSellers = new ArrayList<>();

        for (Object[] row : results) {
            bestSellers.add(PublicBestSellerResponse.builder()
                    .productId(((Number) row[0]).longValue())
                    .productName((String) row[1])
                    .imageUrl((String) row[2])
                    .price(row[3] != null ? ((Number) row[3]).doubleValue() : null)
                    .totalSold(((Number) row[4]).longValue())
                    .build());
        }

        return bestSellers;
    }
}
