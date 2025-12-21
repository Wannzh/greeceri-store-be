package com.greeceri.store.models.response;

import java.time.LocalDateTime;

import com.greeceri.store.models.enums.NotificationType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminNotificationResponse {
    private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private String referenceId;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
