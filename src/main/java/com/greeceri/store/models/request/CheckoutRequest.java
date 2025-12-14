package com.greeceri.store.models.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CheckoutRequest {
    @NotBlank(message = "Address ID is required for checkout")
    private String addressId;

    @NotEmpty(message = "You must select at least one item to checkout")
    private List<Long> selectedCartItemIds;
}
