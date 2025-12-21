package com.greeceri.store.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.greeceri.store.models.entity.AdminNotification;

@Repository
public interface AdminNotificationRepository extends JpaRepository<AdminNotification, Long> {

    // Get all notifications ordered by createdAt desc
    List<AdminNotification> findAllByOrderByCreatedAtDesc();

    // Get paginated notifications
    Page<AdminNotification> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Count unread notifications
    long countByIsReadFalse();

    // Get unread notifications
    List<AdminNotification> findByIsReadFalseOrderByCreatedAtDesc();

    // Mark all as read
    @Modifying
    @Query("UPDATE AdminNotification n SET n.isRead = true WHERE n.isRead = false")
    int markAllAsRead();
}
