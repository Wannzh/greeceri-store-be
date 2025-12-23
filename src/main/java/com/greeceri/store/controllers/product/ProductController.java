package com.greeceri.store.controllers.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greeceri.store.models.entity.Product;
import com.greeceri.store.models.response.GenericResponse;
import com.greeceri.store.models.response.PublicBestSellerResponse;
import com.greeceri.store.services.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<GenericResponse<Page<Product>>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        Page<Product> products = productService.getProducts(page, size, categoryId, keyword);
        return ResponseEntity.ok(new GenericResponse<>(true, "Products retrieved successfully", products));
    }

    @GetMapping("/best-sellers")
    public ResponseEntity<GenericResponse<List<PublicBestSellerResponse>>> getBestSellers() {
        List<PublicBestSellerResponse> bestSellers = productService.getBestSellersThisWeek(4);
        return ResponseEntity.ok(new GenericResponse<>(true, "Best sellers this week", bestSellers));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<GenericResponse<Product>> getProductById(
            @PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(new GenericResponse<>(true, "Product retrieved successfully", product));
    }
}
