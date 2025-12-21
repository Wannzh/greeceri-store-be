package com.greeceri.store.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.greeceri.store.models.entity.AdminNotification;
import com.greeceri.store.models.enums.NotificationType;
import com.greeceri.store.models.response.AdminNotificationResponse;
import com.greeceri.store.repositories.AdminNotificationRepository;
import com.greeceri.store.services.AdminNotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminNotificationServiceImpl implements AdminNotificationService {

    private final AdminNotificationRepository notificationRepository;

    @Override
    public Page<AdminNotificationResponse> getAllNotifications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminNotification> notifications = notificationRepository.findAllByOrderByCreatedAtDesc(pageable);
        return notifications.map(this::mapToResponse);
    }

    @Override
    public long getUnreadCount() {
        return notificationRepository.countByIsReadFalse();
    }

    @Override
    @Transactional
    public AdminNotificationResponse markAsRead(Long notificationId) {
        AdminNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setIsRead(true);
        AdminNotification saved = notificationRepository.save(notification);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public int markAllAsRead() {
        return notificationRepository.markAllAsRead();
    }

    @Override
    @Transactional
    public void createNotification(NotificationType type, String title, String message, String referenceId) {
        AdminNotification notification = AdminNotification.builder()
                .type(type)
                .title(title)
                .message(message)
                .referenceId(referenceId)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }

    // ============ Helper Methods ============

    @Override
    public void notifyNewOrder(String orderId, String customerName, Double totalPrice) {
        String title = "Pesanan Baru";
        String message = String.format("Pesanan baru dari %s senilai Rp %.0f", customerName, totalPrice);
        createNotification(NotificationType.NEW_ORDER, title, message, orderId);
    }

    @Override
    public void notifyPaymentConfirmed(String orderId, Double amount) {
        String title = "Pembayaran Dikonfirmasi";
        String message = String.format("Pembayaran pesanan #%s sebesar Rp %.0f telah dikonfirmasi",
                orderId.substring(0, 8), amount);
        createNotification(NotificationType.PAYMENT_CONFIRMED, title, message, orderId);
    }

    @Override
    public void notifyLowStock(Long productId, String productName, int currentStock) {
        String title = "Stok Menipis";
        String message = String.format("Stok %s tersisa %d unit", productName, currentStock);
        createNotification(NotificationType.LOW_STOCK, title, message, productId.toString());
    }

    @Override
    public void notifyOrderCancelled(String orderId, String reason) {
        String title = "Pesanan Dibatalkan";
        String message = String.format("Pesanan #%s dibatalkan. Alasan: %s", orderId.substring(0, 8), reason);
        createNotification(NotificationType.ORDER_CANCELLED, title, message, orderId);
    }

    @Override
    public void notifyOrderDelivered(String orderId) {
        String title = "Pesanan Diterima";
        String message = String.format("Pesanan #%s telah dikonfirmasi diterima oleh customer",
                orderId.substring(0, 8));
        createNotification(NotificationType.ORDER_DELIVERED, title, message, orderId);
    }

    // ============ Mapper ============

    private AdminNotificationResponse mapToResponse(AdminNotification notification) {
        return AdminNotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .referenceId(notification.getReferenceId())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
