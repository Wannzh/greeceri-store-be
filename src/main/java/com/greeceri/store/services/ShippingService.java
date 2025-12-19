package com.greeceri.store.services;

import java.time.LocalDate;
import java.util.List;

import com.greeceri.store.models.entity.Address;
import com.greeceri.store.models.enums.DeliverySlot;
import com.greeceri.store.models.response.DeliveryValidationResponse;

public interface ShippingService {

    /**
     * Get available delivery slots for a given date
     */
    List<DeliverySlot> getAvailableSlots(LocalDate date);

    /**
     * Validate if an address is within delivery area (5km radius)
     * and calculate shipping cost
     */
    DeliveryValidationResponse validateDeliveryAddress(Address address);

    /**
     * Calculate shipping cost based on distance
     */
    Double calculateShippingCost(double distanceKm);

    /**
     * Validate delivery date (must be today or future)
     */
    boolean isValidDeliveryDate(LocalDate date);
}
