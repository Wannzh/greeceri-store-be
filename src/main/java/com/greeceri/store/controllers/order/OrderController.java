package com.greeceri.store.controllers.order;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.request.CheckoutRequest;
import com.greeceri.store.models.response.OrderResponse;
import com.greeceri.store.services.OrderService;
import com.greeceri.store.models.response.GenericResponse;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<GenericResponse<OrderResponse>> createOrder(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @Valid @RequestBody CheckoutRequest request) {
        User currentUser = (User) currentUserDetails;
        OrderResponse created = orderService.createOrderFromCart(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponse<>(true, "Order created successfully", created));
    }

    @GetMapping("/my")
    public ResponseEntity<GenericResponse<List<OrderResponse>>> getMyOrderHistory(
            @AuthenticationPrincipal UserDetails currentUserDetails) {
        User currentUser = (User) currentUserDetails;
        List<OrderResponse> orders = orderService.getMyOrders(currentUser);
        return ResponseEntity.ok(new GenericResponse<>(true, "Order history retrieved successfully", orders));
    }

    @GetMapping("/my/{orderId}")
    public ResponseEntity<GenericResponse<OrderResponse>> getMyOrderDetails(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable String orderId // ID adalah String (UUID)
    ) {
        User currentUser = (User) currentUserDetails;
        OrderResponse details = orderService.getMyOrderDetails(currentUser, orderId);
        return ResponseEntity.ok(new GenericResponse<>(true, "Order details retrieved successfully", details));
    }
}
