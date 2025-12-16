package com.greeceri.store.controllers.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greeceri.store.models.response.AdminDashboardResponse;
import com.greeceri.store.models.response.GenericResponse;
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
            new GenericResponse<>(true, "Dashboard data retrieved successfully", data)
        );
    }
}