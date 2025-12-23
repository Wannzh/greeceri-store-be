package com.greeceri.store.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.greeceri.store.models.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

        List<Product> findByCategoryId(Long categoryId);

        // Native query for admin product list with pagination and search
        @Query(value = "SELECT p.* FROM products p " +
                        "WHERE (CAST(:keyword AS varchar) IS NULL OR " +
                        "LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS varchar), '%')) OR " +
                        "LOWER(p.description) LIKE LOWER(CONCAT('%', CAST(:keyword AS varchar), '%'))) " +
                        "ORDER BY p.id DESC", countQuery = "SELECT COUNT(*) FROM products p " +
                                        "WHERE (CAST(:keyword AS varchar) IS NULL OR " +
                                        "LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS varchar), '%')) OR " +
                                        "LOWER(p.description) LIKE LOWER(CONCAT('%', CAST(:keyword AS varchar), '%')))", nativeQuery = true)
        Page<Product> findAllByKeyword(
                        @Param("keyword") String keyword,
                        Pageable pageable);

        // Public products with pagination, category filter, and keyword search
        @Query(value = "SELECT p.* FROM products p " +
                        "WHERE (CAST(:categoryId AS bigint) IS NULL OR p.category_id = CAST(:categoryId AS bigint)) " +
                        "AND (CAST(:keyword AS varchar) IS NULL OR " +
                        "LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS varchar), '%')) OR " +
                        "LOWER(p.description) LIKE LOWER(CONCAT('%', CAST(:keyword AS varchar), '%'))) " +
                        "ORDER BY p.id DESC", countQuery = "SELECT COUNT(*) FROM products p " +
                                        "WHERE (CAST(:categoryId AS bigint) IS NULL OR p.category_id = CAST(:categoryId AS bigint)) "
                                        +
                                        "AND (CAST(:keyword AS varchar) IS NULL OR " +
                                        "LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:keyword AS varchar), '%')) OR " +
                                        "LOWER(p.description) LIKE LOWER(CONCAT('%', CAST(:keyword AS varchar), '%')))", nativeQuery = true)
        Page<Product> findAllWithFilter(
                        @Param("categoryId") Long categoryId,
                        @Param("keyword") String keyword,
                        Pageable pageable);
}
