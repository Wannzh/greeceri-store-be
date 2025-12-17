package com.greeceri.store.services;

import java.util.List;

import com.greeceri.store.models.response.AdminDashboardResponse;
import com.greeceri.store.models.response.BestSellerResponse;
import com.greeceri.store.models.response.UserGrowthResponse;

public interface AdminDashboardService {
    AdminDashboardResponse getDashboardSummary();

    List<BestSellerResponse> getBestSellers(int limit);

    List<UserGrowthResponse> getUserGrowth();
}
