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

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @RequestBody CheckoutRequest request
    ) {
        User currentUser = (User) currentUserDetails;
        // Kita return 201 CREATED karena ini membuat resource baru
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrderFromCart(currentUser, request));
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrderHistory(
            @AuthenticationPrincipal UserDetails currentUserDetails
    ) {
        User currentUser = (User) currentUserDetails;
        return ResponseEntity.ok(orderService.getMyOrders(currentUser));
    }

    @GetMapping("/my/{orderId}")
    public ResponseEntity<OrderResponse> getMyOrderDetails(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable String orderId // ID adalah String (UUID)
    ) {
        User currentUser = (User) currentUserDetails;
        return ResponseEntity.ok(orderService.getMyOrderDetails(currentUser, orderId));
    }
}
