package com.greeceri.store.models.request;

import com.greeceri.store.models.enums.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateOrderStatusRequest {
    @NotNull(message = "Status cannot be empty")
    private OrderStatus status;
}