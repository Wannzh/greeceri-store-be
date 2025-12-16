package com.greeceri.store.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.greeceri.store.models.entity.Order;
import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.enums.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUserOrderByOrderDateDesc(User user);

    List<Order> findAllByOrderByOrderDateDesc();

    List<Order> findByStatusOrderByOrderDateDesc(OrderStatus status);

    @Query(value = "SELECT o.* FROM orders o " +
            "JOIN users u ON u.id = o.user_id " +
            "WHERE (CAST(:status AS varchar) IS NULL OR o.status = CAST(:status AS varchar)) " +
            "AND (CAST(:keyword AS varchar) IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS varchar), '%'))) "
            +
            "ORDER BY o.order_date DESC", countQuery = "SELECT COUNT(*) FROM orders o " +
                    "JOIN users u ON u.id = o.user_id " +
                    "WHERE (CAST(:status AS varchar) IS NULL OR o.status = CAST(:status AS varchar)) " +
                    "AND (CAST(:keyword AS varchar) IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS varchar), '%')))", nativeQuery = true)
    Page<Order> findAllByStatusAndKeyword(
            @Param("status") String status,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.status IN ('PAID', 'SHIPPED', 'DELIVERED')")
    Double sumTotalRevenue();

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countOrdersByStatus();

    List<Order> findTop5ByOrderByOrderDateDesc();
}
