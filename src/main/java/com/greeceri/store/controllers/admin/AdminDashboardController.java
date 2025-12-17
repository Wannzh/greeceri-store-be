package com.greeceri.store.controllers.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greeceri.store.models.response.AdminDashboardResponse;
import com.greeceri.store.models.response.BestSellerResponse;
import com.greeceri.store.models.response.GenericResponse;
import com.greeceri.store.models.response.UserGrowthResponse;
import com.greeceri.store.services.AdminDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping
    public ResponseEntity<GenericResponse<AdminDashboardResponse>> getDashboard() {
        AdminDashboardResponse data = adminDashboardService.getDashboardSummary();

        return ResponseEntity.ok(
                new GenericResponse<>(true, "Dashboard data retrieved successfully", data));
    }

    @GetMapping("/best-sellers")
    public ResponseEntity<GenericResponse<List<BestSellerResponse>>> getBestSellers(
            @RequestParam(defaultValue = "5") int limit) {

        List<BestSellerResponse> bestSellers = adminDashboardService.getBestSellers(limit);

        return ResponseEntity.ok(
                new GenericResponse<>(true, "Best selling products retrieved", bestSellers));
    }

    @GetMapping("/user-growth")
    public ResponseEntity<GenericResponse<List<UserGrowthResponse>>> getUserGrowth() {

        List<UserGrowthResponse> userGrowth = adminDashboardService.getUserGrowth();

        return ResponseEntity.ok(
                new GenericResponse<>(true, "User growth data retrieved", userGrowth));
    }
}