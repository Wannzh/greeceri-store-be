package com.greeceri.store.models.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WishlistItemResponse {
    private Long wishlistId;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private Double price;
    private Integer stock;
    private String categoryName;
    private LocalDateTime addedAt;
}
