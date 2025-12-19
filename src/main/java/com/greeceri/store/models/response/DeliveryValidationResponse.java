package com.greeceri.store.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryValidationResponse {
    private boolean isDeliverable;
    private Double distanceKm;
    private Double shippingCost;
    private String message;
}
