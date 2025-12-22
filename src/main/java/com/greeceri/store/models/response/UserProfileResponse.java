package com.greeceri.store.models.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.greeceri.store.models.enums.Gender;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String profileImageUrl;
    private LocalDateTime joinedAt;
}
