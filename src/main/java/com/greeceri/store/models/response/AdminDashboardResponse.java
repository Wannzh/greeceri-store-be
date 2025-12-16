package com.greeceri.store.models.response;

import java.util.List;
import java.util.Map;

import com.greeceri.store.models.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDashboardResponse {
    private long totalOrders;
    private Double totalRevenue;
    private Map<OrderStatus, Long> statusCount;
    private List<AdminOrderSummaryResponse> recentOrders;
}

