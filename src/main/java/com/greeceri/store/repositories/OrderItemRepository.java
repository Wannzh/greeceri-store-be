package com.greeceri.store.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.greeceri.store.models.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Best sellers query - aggregate sales from completed orders
    @Query(value = "SELECT oi.product_id as productId, oi.product_name as productName, " +
            "p.image_url as imageUrl, " +
            "SUM(oi.quantity) as totalSold, " +
            "SUM(oi.quantity * oi.price_at_purchase) as totalRevenue " +
            "FROM order_items oi " +
            "JOIN orders o ON o.id = oi.order_id " +
            "LEFT JOIN products p ON p.id = oi.product_id " +
            "WHERE o.status IN ('PAID', 'SHIPPED', 'DELIVERED') " +
            "GROUP BY oi.product_id, oi.product_name, p.image_url " +
            "ORDER BY totalSold DESC " +
            "LIMIT :limit", nativeQuery = true)
    List<Object[]> findBestSellers(@Param("limit") int limit);
}
