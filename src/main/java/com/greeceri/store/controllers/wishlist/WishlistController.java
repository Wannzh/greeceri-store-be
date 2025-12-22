package com.greeceri.store.controllers.wishlist;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.response.GenericResponse;
import com.greeceri.store.models.response.WishlistItemResponse;
import com.greeceri.store.services.WishlistService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    /**
     * GET /api/wishlist
     * Get all wishlist items for current user
     */
    @GetMapping
    public ResponseEntity<GenericResponse<List<WishlistItemResponse>>> getWishlist(
            @AuthenticationPrincipal UserDetails currentUserDetails) {
        User currentUser = (User) currentUserDetails;
        List<WishlistItemResponse> wishlist = wishlistService.getWishlist(currentUser);
        return ResponseEntity.ok(new GenericResponse<>(true, "Wishlist retrieved successfully", wishlist));
    }

    /**
     * POST /api/wishlist/{productId}
     * Add product to wishlist
     */
    @PostMapping("/{productId}")
    public ResponseEntity<GenericResponse<WishlistItemResponse>> addToWishlist(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable Long productId) {
        User currentUser = (User) currentUserDetails;
        WishlistItemResponse added = wishlistService.addToWishlist(currentUser, productId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponse<>(true, "Produk ditambahkan ke wishlist", added));
    }

    /**
     * DELETE /api/wishlist/{productId}
     * Remove product from wishlist
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<GenericResponse<Void>> removeFromWishlist(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable Long productId) {
        User currentUser = (User) currentUserDetails;
        wishlistService.removeFromWishlist(currentUser, productId);
        return ResponseEntity.ok(new GenericResponse<>(true, "Produk dihapus dari wishlist", null));
    }

    /**
     * GET /api/wishlist/check/{productId}
     * Check if product is in wishlist
     */
    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Boolean>> checkInWishlist(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable Long productId) {
        User currentUser = (User) currentUserDetails;
        boolean isInWishlist = wishlistService.isInWishlist(currentUser, productId);
        return ResponseEntity.ok(Map.of("isInWishlist", isInWishlist));
    }

    /**
     * GET /api/wishlist/count
     * Get wishlist count
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getWishlistCount(
            @AuthenticationPrincipal UserDetails currentUserDetails) {
        User currentUser = (User) currentUserDetails;
        long count = wishlistService.getWishlistCount(currentUser);
        return ResponseEntity.ok(Map.of("count", count));
    }
}
