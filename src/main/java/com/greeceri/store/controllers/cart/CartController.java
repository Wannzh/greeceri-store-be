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
import com.greeceri.store.models.response.GenericResponse;
import com.greeceri.store.models.response.GeneralResponse;
import com.greeceri.store.services.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<GenericResponse<CartResponse>> getMyCart(@AuthenticationPrincipal UserDetails currentUserDetails) {
        User currentUser = (User) currentUserDetails;
        CartResponse cart = cartService.getCart(currentUser);
        return ResponseEntity.ok(new GenericResponse<>(true, "Cart retrieved successfully", cart));
    }

    @PostMapping("/item")
    public ResponseEntity<GenericResponse<CartResponse>> addItemToMyCart(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @Valid @RequestBody CartRequest request) {
        User currentUser = (User) currentUserDetails;

        CartResponse updated = cartService.addItemToCart(currentUser, request);
        return ResponseEntity.ok(new GenericResponse<>(true, "Item added/updated successfully", updated));
    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<GenericResponse<CartResponse>> removeItemMyCart(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable Long cartItemId) {
        User currentUser = (User) currentUserDetails;

        CartResponse updated = cartService.removeItemFromCart(currentUser, cartItemId);
        return ResponseEntity.ok(new GenericResponse<>(true, "Item removed successfully", updated));
    }

    @DeleteMapping
    public ResponseEntity<GenericResponse<CartResponse>> clearMyCart(
            @AuthenticationPrincipal UserDetails currentUserDetails)

    {
        User currentUser = (User) currentUserDetails;
        CartResponse cleared = cartService.clearCart(currentUser);
        return ResponseEntity.ok(new GenericResponse<>(true, "Cart cleared successfully", cleared));
    }
}
