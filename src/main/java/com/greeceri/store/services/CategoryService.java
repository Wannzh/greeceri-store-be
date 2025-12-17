package com.greeceri.store.services;

import java.util.List;

import com.greeceri.store.models.response.CategoryResponse;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(Long categoryId);
}
