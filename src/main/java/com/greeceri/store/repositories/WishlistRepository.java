package com.greeceri.store.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.greeceri.store.models.entity.Product;
import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.entity.Wishlist;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // Get all wishlist items for a user, ordered by createdAt DESC
    List<Wishlist> findByUserOrderByCreatedAtDesc(User user);

    // Check if product is in user's wishlist
    Optional<Wishlist> findByUserAndProduct(User user, Product product);

    // Check if exists
    boolean existsByUserAndProduct(User user, Product product);

    // Delete by user and product
    void deleteByUserAndProduct(User user, Product product);

    // Count user's wishlist
    long countByUser(User user);
}
