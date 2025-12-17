package com.greeceri.store.models.response;

import java.time.LocalDateTime;

import com.greeceri.store.models.enums.Role;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserSummaryResponse {
    private String id;
    private String name;
    private String email;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;
    private Long totalOrders;
}
