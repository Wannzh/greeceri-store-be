package com.greeceri.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.greeceri.store.models.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, String> {

}
