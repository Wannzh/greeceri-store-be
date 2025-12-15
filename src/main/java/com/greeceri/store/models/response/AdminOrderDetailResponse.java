package com.greeceri.store.models.response;

import java.time.LocalDateTime;
import java.util.List;

import com.greeceri.store.models.enums.OrderStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminOrderDetailResponse {
    private String id;
    private OrderStatus status;
    private Double totalPrice;
    private LocalDateTime createdAt;
    private AdminUserInfo user;
    private AdminAddressInfo shippingAddress;
    private List<AdminItemInfo> items;

    @Data
    @Builder
    public static class AdminUserInfo {
        private String name;
        private String email;
    }

    @Data
    @Builder
    public static class AdminAddressInfo {
        private String receiverName;
        private String phoneNumber;
        private String fullAddress;
    }

    @Data
    @Builder
    public static class AdminItemInfo {
        private String productName;
        private Double price;
        private int quantity;
        private Double subtotal;
    }
}