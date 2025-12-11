package com.greeceri.store.controllers.product;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greeceri.store.models.entity.Product;
import com.greeceri.store.models.response.GenericResponse;
import com.greeceri.store.services.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<GenericResponse<List<Product>>> getAllProducts(
            @RequestParam(required = false) Long categoryId) {
        List<Product> products;
        if (categoryId != null) {
            products = productService.getProductsByCategoryId(categoryId);
        } else {
            products = productService.getAllProducts();
        }
        return ResponseEntity.ok(new GenericResponse<>(true, "Products retrieved successfully", products));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<GenericResponse<Product>> getProductById(
            @PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(new GenericResponse<>(true, "Product retrieved successfully", product));
    }
}
