package com.greeceri.store.models.response;

import java.time.LocalDateTime;

import com.greeceri.store.models.enums.OrderStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminOrderSummaryResponse {
    private String id;
    private String userName;
    private Double totalPrice;
    private OrderStatus status;
    private LocalDateTime createdAt;
}