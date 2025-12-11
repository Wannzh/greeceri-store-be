package com.greeceri.store.models.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckoutRequest {
    @NotBlank(message = "Address ID is required for checkout")
    private String addressId;
}
