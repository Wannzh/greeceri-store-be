package com.greeceri.store.controllers.admin;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greeceri.store.models.enums.OrderStatus;
import com.greeceri.store.models.request.UpdateOrderStatusRequest;
import com.greeceri.store.models.response.AdminOrderDetailResponse;
import com.greeceri.store.models.response.AdminOrderSummaryResponse;
import com.greeceri.store.models.response.GenericResponse;
import com.greeceri.store.services.AdminOrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    // GET /api/admin/orders
    @GetMapping
    public ResponseEntity<GenericResponse<Page<AdminOrderSummaryResponse>>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {

        OrderStatus parsedStatus = null;

        if (status != null && !status.isBlank()) {
            try {
                parsedStatus = OrderStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid order status: " + status);
            }
        }

        Page<AdminOrderSummaryResponse> orders = adminOrderService.getOrders(page, size, parsedStatus, keyword);

        return ResponseEntity.ok(
                new GenericResponse<>(true, "Orders retrieved successfully", orders));
    }

    // GET /api/admin/orders/{orderId}
    @GetMapping("/{orderId}")
    public ResponseEntity<GenericResponse<AdminOrderDetailResponse>> getOrderById(@PathVariable String orderId) {
        AdminOrderDetailResponse orderDetail = adminOrderService.getOrderById(orderId);
        return ResponseEntity.ok(new GenericResponse<>(true, "Order detail retrieved successfully", orderDetail));
    }

    // PUT /api/admin/orders/{orderId}/status
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Map<String, String>> updateOrderStatus(
            @PathVariable String orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        Map<String, String> result = adminOrderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(result);
    }
}