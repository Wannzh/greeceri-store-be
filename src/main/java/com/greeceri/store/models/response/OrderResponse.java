package com.greeceri.store.models.response;

import java.time.LocalDateTime;
import java.util.List;

import com.greeceri.store.models.enums.OrderStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
    private String orderId;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private Double totalPrice;
    private AddressResponse shippingAddress;
    private List<OrderItemResponse> items;
}
