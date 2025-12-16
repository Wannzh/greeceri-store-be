package com.greeceri.store.services;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.greeceri.store.models.enums.OrderStatus;
import com.greeceri.store.models.request.UpdateOrderStatusRequest;
import com.greeceri.store.models.response.AdminOrderDetailResponse;
import com.greeceri.store.models.response.AdminOrderSummaryResponse;

public interface AdminOrderService {
    List<AdminOrderSummaryResponse> getAllOrders();
    List<AdminOrderSummaryResponse> getOrdersByStatus(String status);
    AdminOrderDetailResponse getOrderById(String orderId);
    Map<String, String> updateOrderStatus(String orderId, UpdateOrderStatusRequest request);
    Page<AdminOrderSummaryResponse> getOrders(int page, int size, OrderStatus status, String keyword);
}