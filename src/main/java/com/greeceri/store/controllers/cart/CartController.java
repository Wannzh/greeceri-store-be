package com.greeceri.store.controllers.cart;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.request.CartRequest;
import com.greeceri.store.models.response.CartResponse;
import com.greeceri.store.services.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponse> getMyCart(@AuthenticationPrincipal UserDetails currentUserDetails) {
        User currentUser = (User) currentUserDetails;
        return ResponseEntity.ok(cartService.getCart(currentUser));
    }

    @PostMapping("/item")
    public ResponseEntity<CartResponse> addItemToMyCart(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @RequestBody CartRequest request) {
        User currentUser = (User) currentUserDetails;

        return ResponseEntity.ok(cartService.addItemToCart(currentUser, request));
    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<CartResponse> removeItemMyCart(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable Long cartItemId) {
        User currentUser = (User) currentUserDetails;

        return ResponseEntity.ok(cartService.removeItemFromCart(currentUser, cartItemId));
    }

    @DeleteMapping
    public ResponseEntity<CartResponse> clearMyCart(
            @AuthenticationPrincipal UserDetails currentUserDetails)

    {
        User currentUser = (User) currentUserDetails;
        return ResponseEntity.ok(cartService.clearCart(currentUser));
    }
}
