package com.greeceri.store.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.greeceri.store.models.entity.Cart;
import com.greeceri.store.models.entity.CartItem;
import com.greeceri.store.models.entity.Product;
import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.request.CartRequest;
import com.greeceri.store.models.response.CartItemResponse;
import com.greeceri.store.models.response.CartResponse;
import com.greeceri.store.repositories.CartItemRepository;
import com.greeceri.store.repositories.CartRepository;
import com.greeceri.store.repositories.ProductRepository;
import com.greeceri.store.services.CartService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public CartResponse getCart(User currentUser) {
        Cart cart = getOrCreateCart(currentUser);

        return mapCartToResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addItemToCart(User currentUser, CartRequest request) {
        Cart cart = getOrCreateCart(currentUser);

        // Validasi Product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product Not Found"));

        // Item Product Validasi
        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("insufficient stock");
        }

        // Cek Produk sudah ada di cart
        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItemOpt.isPresent()) {
            // Produk sudah ada
            CartItem existingItem = existingItemOpt.get();

            if (request.getQuantity() <= 0) {
                // Remove jika kuantitas 0
                cartItemRepository.delete(existingItem);
            } else {
                // Update kuantitas di cart
                existingItem.setQuantity(request.getQuantity());
                cartItemRepository.save(existingItem);
            }
        } else {
            // new produk
            if (request.getQuantity() > 0) {
                CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
                cartItemRepository.save(newItem);
            }
        }

        // Muat ulang cart dari DB untuk mendapatkan data terbaru
        Cart updatedCart = cartRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
                
        return mapCartToResponse(updatedCart);
    }

    @Override
    @Transactional
    public CartResponse removeItemFromCart(User currentUser, Long cartItemId) {
        Cart cart = getOrCreateCart(currentUser);

        // Validasi Cart User
        CartItem itemToRemove = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!itemToRemove.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Access denied. This item is not yours.");
        }

        // Hapus Item
        cartItemRepository.delete(itemToRemove);

        return mapCartToResponse(cartRepository.findByUser(currentUser).get());
    }

    @Override
    @Transactional
    public CartResponse clearCart(User currentUser) {
        Cart cart = getOrCreateCart(currentUser);

        cartItemRepository.deleteAll(cart.getItems());

        return mapCartToResponse(cartRepository.findByUser(currentUser).get());
    }

    private Cart getOrCreateCart(User user) {
        Optional<Cart> cartOpt = cartRepository.findByUser(user);

        if (cartOpt.isPresent()) {
            return cartOpt.get();
        } else {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        }
    }

    private CartResponse mapCartToResponse(Cart cart) {
        List<CartItemResponse> itemResponses = new ArrayList<>();
        double grandTotal = 0.0;
        int totalItems = 0;

        for (CartItem item : cart.getItems()) {
            double subTotal = item.getProduct().getPrice() * item.getQuantity();
            itemResponses.add(CartItemResponse.builder()
                    .cartItemId(item.getId())
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .productImageUrl(item.getProduct().getImageUrl())
                    .price(item.getProduct().getPrice())
                    .quantity(item.getQuantity())
                    .subTotal(subTotal)
                    .build());

            grandTotal += subTotal;
            totalItems += item.getQuantity();

        }

        return CartResponse.builder()
                .cartId(cart.getId())
                .items(itemResponses)
                .totalItems(totalItems)
                .grandTotal(grandTotal)
                .build();
    }

}
