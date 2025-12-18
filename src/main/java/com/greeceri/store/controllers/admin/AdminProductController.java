package com.greeceri.store.controllers.admin;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greeceri.store.models.request.AdminProductRequest;
import com.greeceri.store.models.response.AdminProductSummaryResponse;
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

    // GET /api/admin/products - List all products with pagination and search
    @GetMapping
    public ResponseEntity<GenericResponse<Page<AdminProductSummaryResponse>>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        Page<AdminProductSummaryResponse> products = adminProductService.getProducts(page, size, keyword);

        return ResponseEntity.ok(
                new GenericResponse<>(true, "Products retrieved successfully", products));
    }

    @PostMapping
    public ResponseEntity<GenericResponse<AdminProductSummaryResponse>> createProduct(
            @Valid @RequestBody AdminProductRequest request) {
        AdminProductSummaryResponse newProduct = adminProductService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponse<>(true, "Product created successfully", newProduct));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<GenericResponse<AdminProductSummaryResponse>> updateProduct(
            @PathVariable Long productId,
            @Valid @RequestBody AdminProductRequest request) {
        AdminProductSummaryResponse updatedProduct = adminProductService.updateProduct(productId, request);
        return ResponseEntity.ok(new GenericResponse<>(true, "Product updated successfully", updatedProduct));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<GeneralResponse> deleteProduct(
            @PathVariable Long productId) {
        adminProductService.deleteProduct(productId);
        return ResponseEntity.ok(new GeneralResponse(true, "Product deleted successfully"));
    }
}
