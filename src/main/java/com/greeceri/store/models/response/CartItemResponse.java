package com.greeceri.store.models.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponse {
    private Long cartItemId;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private Double price;
    private int quantity;
    private Double subTotal;

}
