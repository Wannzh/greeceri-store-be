package com.greeceri.store.models.request;

import java.time.LocalDate;

import com.greeceri.store.models.enums.Gender;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    private String phoneNumber;
    private Gender gender;
    private LocalDate dateOfBirth;
}
