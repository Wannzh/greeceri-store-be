package com.greeceri.store.models.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminProductRequest {
    @NotBlank(message = "The product name cannot be empty")
    private String name;

    private String description;

    @NotNull(message = "Price cannot be blank")
    @Min(value = 1, message = "Price must be more than 0")
    private Double price;

    @NotNull(message = "Stock must not be empty")
    @Min(value = 0, message = "Stock must not be negative")
    private Integer stock;

    private String imageUrl;

    @NotNull(message = "Category ID cannot be empty")
    private Long categoryId; // Kita hanya perlu ID kategori
}
