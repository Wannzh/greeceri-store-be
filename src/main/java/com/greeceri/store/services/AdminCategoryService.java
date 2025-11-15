package com.greeceri.store.services;

import com.greeceri.store.models.entity.Category;
import com.greeceri.store.models.request.AdminCategoryRequest;

public interface AdminCategoryService {
    Category createCategory(AdminCategoryRequest request);
    Category updateCategory(Long categoryId, AdminCategoryRequest request);
    void deleteCategory(Long categoryId);
}
