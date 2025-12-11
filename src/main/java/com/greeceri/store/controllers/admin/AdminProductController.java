package com.greeceri.store.controllers.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greeceri.store.models.entity.Product;
import com.greeceri.store.models.request.AdminProductRequest;
import com.greeceri.store.models.response.GenericResponse;
import com.greeceri.store.models.response.GeneralResponse;
import com.greeceri.store.services.AdminProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
    private final AdminProductService adminProductService;

    @PostMapping
    public ResponseEntity<GenericResponse<Product>> createProduct(
            @Valid @RequestBody AdminProductRequest request) {
        Product newProduct = adminProductService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponse<>(true, "Product created successfully", newProduct));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<GenericResponse<Product>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody AdminProductRequest request) {
        Product updatedProduct = adminProductService.updateProduct(productId, request);
        return ResponseEntity.ok(new GenericResponse<>(true, "Product updated successfully", updatedProduct));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<GeneralResponse> deleteProduct(
            @PathVariable Long productId) {
        adminProductService.deleteProduct(productId);
        return ResponseEntity.ok(new GeneralResponse(true, "Product deleted successfully"));
    }
}
