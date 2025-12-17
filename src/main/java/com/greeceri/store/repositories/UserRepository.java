package com.greeceri.store.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.greeceri.store.models.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByVerificationToken(String token);

    // Native query for admin user list with pagination, search, and role filter
    @Query(value = "SELECT u.* FROM users u " +
            "WHERE (CAST(:role AS varchar) IS NULL OR u.role = CAST(:role AS varchar)) " +
            "AND (CAST(:keyword AS varchar) IS NULL OR " +
            "LOWER(u.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS varchar), '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:keyword AS varchar), '%'))) " +
            "ORDER BY u.name ASC", countQuery = "SELECT COUNT(*) FROM users u " +
                    "WHERE (CAST(:role AS varchar) IS NULL OR u.role = CAST(:role AS varchar)) " +
                    "AND (CAST(:keyword AS varchar) IS NULL OR " +
                    "LOWER(u.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS varchar), '%')) OR " +
                    "LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:keyword AS varchar), '%')))", nativeQuery = true)
    Page<User> findAllByRoleAndKeyword(
            @Param("role") String role,
            @Param("keyword") String keyword,
            Pageable pageable);

    // Count total orders by user
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
    Long countOrdersByUserId(@Param("userId") String userId);

    // Calculate total spent by user (only completed orders)
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o " +
            "WHERE o.user.id = :userId AND o.status IN (com.greeceri.store.models.enums.OrderStatus.PAID, " +
            "com.greeceri.store.models.enums.OrderStatus.SHIPPED, com.greeceri.store.models.enums.OrderStatus.DELIVERED)")
    Double sumTotalSpentByUserId(@Param("userId") String userId);
}
