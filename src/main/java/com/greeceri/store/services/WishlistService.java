package com.greeceri.store.services;

import java.util.List;

import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.response.WishlistItemResponse;

public interface WishlistService {

    // Get all wishlist items for a user
    List<WishlistItemResponse> getWishlist(User user);

    // Add product to wishlist
    WishlistItemResponse addToWishlist(User user, Long productId);

    // Remove product from wishlist
    void removeFromWishlist(User user, Long productId);

    // Check if product is in wishlist
    boolean isInWishlist(User user, Long productId);

    // Get wishlist count
    long getWishlistCount(User user);
}
