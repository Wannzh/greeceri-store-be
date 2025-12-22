package com.greeceri.store.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.greeceri.store.models.entity.Product;
import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.entity.Wishlist;
import com.greeceri.store.models.response.WishlistItemResponse;
import com.greeceri.store.repositories.ProductRepository;
import com.greeceri.store.repositories.WishlistRepository;
import com.greeceri.store.services.WishlistService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    @Override
    public List<WishlistItemResponse> getWishlist(User user) {
        List<Wishlist> wishlists = wishlistRepository.findByUserOrderByCreatedAtDesc(user);
        return wishlists.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public WishlistItemResponse addToWishlist(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));

        // Check if already in wishlist
        if (wishlistRepository.existsByUserAndProduct(user, product)) {
            throw new RuntimeException("Produk sudah ada di wishlist");
        }

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .build();

        Wishlist saved = wishlistRepository.save(wishlist);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void removeFromWishlist(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produk tidak ditemukan"));

        Wishlist wishlist = wishlistRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new RuntimeException("Produk tidak ada di wishlist"));

        wishlistRepository.delete(wishlist);
    }

    @Override
    public boolean isInWishlist(User user, Long productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return false;
        }
        return wishlistRepository.existsByUserAndProduct(user, product);
    }

    @Override
    public long getWishlistCount(User user) {
        return wishlistRepository.countByUser(user);
    }

    private WishlistItemResponse mapToResponse(Wishlist wishlist) {
        Product product = wishlist.getProduct();
        String categoryName = product.getCategory() != null ? product.getCategory().getName() : null;

        return WishlistItemResponse.builder()
                .wishlistId(wishlist.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImageUrl(product.getImageUrl())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryName(categoryName)
                .addedAt(wishlist.getCreatedAt())
                .build();
    }
}
