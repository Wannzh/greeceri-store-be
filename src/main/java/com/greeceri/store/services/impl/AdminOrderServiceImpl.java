package com.greeceri.store.services.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.greeceri.store.models.entity.Order;
import com.greeceri.store.models.enums.OrderStatus;
import com.greeceri.store.models.request.UpdateOrderStatusRequest;
import com.greeceri.store.models.response.AdminOrderDetailResponse;
import com.greeceri.store.models.response.AdminOrderSummaryResponse;
import com.greeceri.store.repositories.OrderRepository;
import com.greeceri.store.services.AdminOrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

    private final OrderRepository orderRepository;

    @Override
    public List<AdminOrderSummaryResponse> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc().stream()
                .map(order -> AdminOrderSummaryResponse.builder()
                        .id(order.getId())
                        .userName(order.getUser().getName())
                        .totalPrice(order.getTotalPrice())
                        .status(order.getStatus())
                        .createdAt(order.getOrderDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Page<AdminOrderSummaryResponse> getOrders(int page, int size, OrderStatus statusStr, String keyword) {
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("orderDate").descending());

        Page<Order> orderPage = orderRepository.findAllByStatusAndKeyword(statusStr, keyword, pageable);

        return orderPage.map(this::mapToSummaryResponse);
    }

    @Override
    public List<AdminOrderSummaryResponse> getOrdersByStatus(String statusStr) {
        try {
            OrderStatus status = OrderStatus.valueOf(statusStr.toUpperCase());

            return orderRepository.findByStatusOrderByOrderDateDesc(status).stream()
                    .map(this::mapToSummaryResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + statusStr);
        }
    }

    @Override
    public AdminOrderDetailResponse getOrderById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Mapping Nested Objects
        AdminOrderDetailResponse.AdminUserInfo userInfo = AdminOrderDetailResponse.AdminUserInfo.builder()
                .name(order.getUser().getName())
                .email(order.getUser().getEmail())
                .build();

        AdminOrderDetailResponse.AdminAddressInfo addressInfo = AdminOrderDetailResponse.AdminAddressInfo.builder()
                .receiverName(order.getShippingAddress().getReceiverName())
                .phoneNumber(order.getShippingAddress().getPhoneNumber())
                .fullAddress(order.getShippingAddress().getFullAddress())
                .build();

        List<AdminOrderDetailResponse.AdminItemInfo> itemInfos = order.getItems().stream()
                .map(item -> AdminOrderDetailResponse.AdminItemInfo.builder()
                        .productName(item.getProductName())
                        .price(item.getPriceAtPurchase())
                        .quantity(item.getQuantity())
                        .subtotal(item.getPriceAtPurchase() * item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return AdminOrderDetailResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getOrderDate())
                .user(userInfo)
                .shippingAddress(addressInfo)
                .items(itemInfos)
                .build();
    }

    @Override
    @Transactional
    public Map<String, String> updateOrderStatus(String orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus currentStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();

        if (!OrderStatus.isValidTransition(currentStatus, newStatus)) {
            throw new IllegalStateException(
                    "Invalid status transition: " + currentStatus + " → " + newStatus);
        }

        order.setStatus(newStatus);
        orderRepository.save(order);

        return Map.of(
                "message", "Order status updated successfully",
                "status", newStatus.name());
    }

    private AdminOrderSummaryResponse mapToSummaryResponse(Order order) {
        return AdminOrderSummaryResponse.builder()
                .id(order.getId())
                .userName(order.getUser().getName())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getOrderDate())
                .build();
    }
}