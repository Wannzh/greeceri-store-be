package com.greeceri.store.controllers.admin;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greeceri.store.models.response.AdminNotificationResponse;
import com.greeceri.store.models.response.GenericResponse;
import com.greeceri.store.services.AdminNotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/notifications")
public class AdminNotificationController {

    private final AdminNotificationService notificationService;

    /**
     * GET /api/admin/notifications
     * List all notifications with pagination
     */
    @GetMapping
    public ResponseEntity<GenericResponse<Page<AdminNotificationResponse>>> getAllNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<AdminNotificationResponse> notifications = notificationService.getAllNotifications(page, size);
        return ResponseEntity.ok(new GenericResponse<>(true, "Notifications retrieved successfully", notifications));
    }

    /**
     * GET /api/admin/notifications/unread-count
     * Get count of unread notifications
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        long count = notificationService.getUnreadCount();
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    /**
     * PUT /api/admin/notifications/{id}/read
     * Mark single notification as read
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<GenericResponse<AdminNotificationResponse>> markAsRead(@PathVariable Long id) {
        AdminNotificationResponse notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(new GenericResponse<>(true, "Notification marked as read", notification));
    }

    /**
     * PUT /api/admin/notifications/read-all
     * Mark all notifications as read
     */
    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead() {
        int count = notificationService.markAllAsRead();
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "All notifications marked as read",
                "updatedCount", count));
    }
}
