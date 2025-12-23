package com.greeceri.store.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublicBestSellerResponse {
    private Long productId;
    private String productName;
    private String imageUrl;
    private Double price;
    private Long totalSold;
}
