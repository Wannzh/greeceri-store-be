package com.greeceri.store.models.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminCategoryRequest {
    @NotBlank(message = "The category name cannot be empty")
    private String name;
    private String description;
}
