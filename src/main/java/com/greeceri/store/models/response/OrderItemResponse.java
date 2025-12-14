package com.greeceri.store.models.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemResponse {
    private Long productId;
    private String productName;
    private String productImageUrl;
    private Double priceAtPurchase;
    private int quantity;
    private Double subTotal;
}
