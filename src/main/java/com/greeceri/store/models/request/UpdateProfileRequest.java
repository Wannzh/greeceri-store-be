package com.greeceri.store.models.request;

import java.time.LocalDate;

import com.greeceri.store.models.enums.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9\\-\\+\\(\\)\\s]+$", message = "Phone number format is invalid")
    private String phoneNumber;
    
    private Gender gender;
    private LocalDate dateOfBirth;
}
