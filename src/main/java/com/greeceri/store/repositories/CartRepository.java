package com.greeceri.store.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.greeceri.store.models.entity.Cart;
import com.greeceri.store.models.entity.User;

public interface CartRepository extends JpaRepository<Cart, String> {
    Optional<Cart> findByUser(User user);
}
