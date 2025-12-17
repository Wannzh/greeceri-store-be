package com.greeceri.store.models.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminProductSummaryResponse {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private String categoryName;
    private Long categoryId;
}
