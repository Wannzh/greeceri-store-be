package com.greeceri.store.services.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.greeceri.store.models.enums.OrderStatus;
import com.greeceri.store.models.response.AdminDashboardResponse;
import com.greeceri.store.models.response.AdminOrderSummaryResponse;
import com.greeceri.store.repositories.OrderRepository;
import com.greeceri.store.services.AdminDashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final OrderRepository orderRepository;

    @Override
    public AdminDashboardResponse getDashboardSummary() {
        long totalOrders = orderRepository.count();

        Double totalRevenue = orderRepository.sumTotalRevenue();

        List<Object[]> statusCountsData = orderRepository.countOrdersByStatus();
        Map<OrderStatus, Long> statusCountMap = new HashMap<>();
        
        for (OrderStatus status : OrderStatus.values()) {
            statusCountMap.put(status, 0L);
        }
        
        for (Object[] row : statusCountsData) {
            statusCountMap.put((OrderStatus) row[0], (Long) row[1]);
        }

        List<AdminOrderSummaryResponse> recentOrders = orderRepository.findTop5ByOrderByOrderDateDesc()
                .stream()
                .map(order -> AdminOrderSummaryResponse.builder()
                        .id(order.getId())
                        .userName(order.getUser().getName())
                        .totalPrice(order.getTotalPrice())
                        .status(order.getStatus())
                        .createdAt(order.getOrderDate())
                        .build())
                .collect(Collectors.toList());

        return AdminDashboardResponse.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .statusCount(statusCountMap)
                .recentOrders(recentOrders)
                .build();
    }
}