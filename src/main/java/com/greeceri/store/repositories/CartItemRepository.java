package com.greeceri.store.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.greeceri.store.models.entity.Cart;
import com.greeceri.store.models.entity.CartItem;
import com.greeceri.store.models.entity.Product;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
