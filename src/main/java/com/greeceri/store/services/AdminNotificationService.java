package com.greeceri.store.services;

import org.springframework.data.domain.Page;

import com.greeceri.store.models.enums.NotificationType;
import com.greeceri.store.models.response.AdminNotificationResponse;

public interface AdminNotificationService {

    // Get all notifications with pagination
    Page<AdminNotificationResponse> getAllNotifications(int page, int size);

    // Get unread count
    long getUnreadCount();

    // Mark single notification as read
    AdminNotificationResponse markAsRead(Long notificationId);

    // Mark all notifications as read
    int markAllAsRead();

    // Create notification (used internally)
    void createNotification(NotificationType type, String title, String message, String referenceId);

    // Helper methods for specific notification types
    void notifyNewOrder(String orderId, String customerName, Double totalPrice);

    void notifyPaymentConfirmed(String orderId, Double amount);

    void notifyLowStock(Long productId, String productName, int currentStock);

    void notifyOrderCancelled(String orderId, String reason);

    void notifyOrderDelivered(String orderId);
}
