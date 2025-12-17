package com.greeceri.store.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BestSellerResponse {
    private Long productId;
    private String productName;
    private String imageUrl;
    private Long totalSold;
    private Double totalRevenue;
}
