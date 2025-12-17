package com.greeceri.store.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.greeceri.store.models.entity.Category;
import com.greeceri.store.repositories.CategoryRepository;
import com.greeceri.store.services.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
    }
}
