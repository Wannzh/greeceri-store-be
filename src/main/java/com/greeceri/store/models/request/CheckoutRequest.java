package com.greeceri.store.models.request;

import java.time.LocalDate;
import java.util.List;

import com.greeceri.store.models.enums.DeliverySlot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutRequest {
    @NotBlank(message = "Address ID is required for checkout")
    private String addressId;

    @NotEmpty(message = "You must select at least one item to checkout")
    private List<Long> selectedCartItemIds;

    @NotNull(message = "Delivery date is required")
    private LocalDate deliveryDate;

    @NotNull(message = "Delivery slot is required")
    private DeliverySlot deliverySlot;
}
