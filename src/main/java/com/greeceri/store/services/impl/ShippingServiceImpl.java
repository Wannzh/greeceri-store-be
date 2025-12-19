package com.greeceri.store.services.impl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.greeceri.store.models.entity.Address;
import com.greeceri.store.models.enums.DeliverySlot;
import com.greeceri.store.models.response.DeliveryValidationResponse;
import com.greeceri.store.services.ShippingService;

@Service
public class ShippingServiceImpl implements ShippingService {

    // Store location: Dakota, Sukaraja, Bandung
    private static final double STORE_LATITUDE = -6.893659413861898;
    private static final double STORE_LONGITUDE = 107.57084788978071;

    // Maximum delivery radius in kilometers
    private static final double MAX_DELIVERY_RADIUS_KM = 5.0;

    // Earth radius in kilometers (for Haversine formula)
    private static final double EARTH_RADIUS_KM = 6371.0;

    @Override
    public List<DeliverySlot> getAvailableSlots(LocalDate date) {
        // All slots are available for any valid date
        // In the future, you can add logic to disable slots based on capacity
        return Arrays.asList(DeliverySlot.values());
    }

    @Override
    public DeliveryValidationResponse validateDeliveryAddress(Address address) {
        // Check if coordinates are provided
        if (address.getLatitude() == null || address.getLongitude() == null) {
            return DeliveryValidationResponse.builder()
                    .isDeliverable(false)
                    .distanceKm(null)
                    .shippingCost(null)
                    .message("Koordinat alamat belum tersedia. Silakan update alamat dengan lokasi yang valid.")
                    .build();
        }

        // Calculate distance using Haversine formula
        double distance = calculateDistance(
                STORE_LATITUDE, STORE_LONGITUDE,
                address.getLatitude(), address.getLongitude());

        // Round to 2 decimal places
        distance = Math.round(distance * 100.0) / 100.0;

        // Check if within delivery radius
        if (distance > MAX_DELIVERY_RADIUS_KM) {
            return DeliveryValidationResponse.builder()
                    .isDeliverable(false)
                    .distanceKm(distance)
                    .shippingCost(null)
                    .message(String.format(
                            "Alamat di luar jangkauan pengiriman (%.2f km). Maksimal radius pengiriman adalah %.0f km dari toko.",
                            distance, MAX_DELIVERY_RADIUS_KM))
                    .build();
        }

        // Calculate shipping cost based on distance
        Double shippingCost = calculateShippingCost(distance);

        return DeliveryValidationResponse.builder()
                .isDeliverable(true)
                .distanceKm(distance)
                .shippingCost(shippingCost)
                .message(String.format("Alamat dapat dijangkau (%.2f km). Ongkos kirim: Rp %.0f", distance,
                        shippingCost))
                .build();
    }

    @Override
    public Double calculateShippingCost(double distanceKm) {
        // Tiered pricing based on distance
        // 0 - 2 km: Rp 5.000
        // 2 - 3 km: Rp 7.000
        // 3 - 4 km: Rp 9.000
        // 4 - 5 km: Rp 12.000

        if (distanceKm <= 2.0) {
            return 5000.0;
        } else if (distanceKm <= 3.0) {
            return 7000.0;
        } else if (distanceKm <= 4.0) {
            return 9000.0;
        } else {
            return 12000.0;
        }
    }

    @Override
    public boolean isValidDeliveryDate(LocalDate date) {
        // Delivery date must be today or in the future
        return date != null && !date.isBefore(LocalDate.now());
    }

    /**
     * Calculate distance between two coordinates using Haversine formula
     * 
     * @return distance in kilometers
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
