package com.greeceri.store.models.request;

import lombok.Data;

@Data
public class CartRequest {
    private Long productId;
    private int quantity;
}
