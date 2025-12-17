package com.greeceri.store.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.greeceri.store.models.enums.OrderStatus;
import com.greeceri.store.models.response.AdminDashboardResponse;
import com.greeceri.store.models.response.AdminOrderSummaryResponse;
import com.greeceri.store.models.response.BestSellerResponse;
import com.greeceri.store.models.response.UserGrowthResponse;
import com.greeceri.store.repositories.CategoryRepository;
import com.greeceri.store.repositories.OrderItemRepository;
import com.greeceri.store.repositories.OrderRepository;
import com.greeceri.store.repositories.ProductRepository;
import com.greeceri.store.repositories.UserRepository;
import com.greeceri.store.services.AdminDashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    @Override
    public AdminDashboardResponse getDashboardSummary() {
        long totalOrders = orderRepository.count();

        Double totalRevenue = orderRepository.sumTotalRevenue();

        Long totalProducts = productRepository.count();
        Long totalCategories = categoryRepository.count();

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
                .totalProducts(totalProducts)
                .totalCategories(totalCategories)
                .statusCount(statusCountMap)
                .recentOrders(recentOrders)
                .build();
    }

    @Override
    public List<BestSellerResponse> getBestSellers(int limit) {
        List<Object[]> results = orderItemRepository.findBestSellers(limit);
        List<BestSellerResponse> bestSellers = new ArrayList<>();

        for (Object[] row : results) {
            bestSellers.add(BestSellerResponse.builder()
                    .productId(((Number) row[0]).longValue())
                    .productName((String) row[1])
                    .imageUrl((String) row[2])
                    .totalSold(((Number) row[3]).longValue())
                    .totalRevenue(((Number) row[4]).doubleValue())
                    .build());
        }

        return bestSellers;
    }

    @Override
    public List<UserGrowthResponse> getUserGrowth() {
        List<Object[]> results = userRepository.findUserGrowthByMonth();
        List<UserGrowthResponse> userGrowth = new ArrayList<>();

        for (Object[] row : results) {
            userGrowth.add(UserGrowthResponse.builder()
                    .month((String) row[0])
                    .count(((Number) row[1]).longValue())
                    .build());
        }

        return userGrowth;
    }
}