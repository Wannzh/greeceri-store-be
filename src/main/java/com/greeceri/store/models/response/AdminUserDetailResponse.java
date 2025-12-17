package com.greeceri.store.models.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.greeceri.store.models.enums.Gender;
import com.greeceri.store.models.enums.Role;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUserDetailResponse {
    private String id;
    private String name;
    private String email;
    private Role role;
    private boolean enabled;
    private LocalDateTime createdAt;
    private String phoneNumber;
    private Gender gender;
    private LocalDate dateOfBirth;
    private Long orderCount;
    private Double totalSpent;
}
