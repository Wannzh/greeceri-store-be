package com.greeceri.store.models.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartResponse {
    private String cartId;
    private List<CartItemResponse> items;
    private int totalItems;
    private Double grandTotal;
}
