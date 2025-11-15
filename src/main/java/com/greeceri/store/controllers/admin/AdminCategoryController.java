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

import com.greeceri.store.models.entity.Category;
import com.greeceri.store.models.request.AdminCategoryRequest;
import com.greeceri.store.services.AdminCategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final AdminCategoryService adminCategoryService;

    @PostMapping
    public ResponseEntity<Category> createCategory(
            @Valid @RequestBody AdminCategoryRequest request
    ) {
        Category newCategory = adminCategoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCategory);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody AdminCategoryRequest request
    ) {
        Category updatedCategory = adminCategoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long categoryId
    ) {
        adminCategoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
