package com.greeceri.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.greeceri.store.models.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

}
