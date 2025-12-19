package com.greeceri.store.controllers.shipping;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greeceri.store.models.entity.Address;
import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.enums.DeliverySlot;
import com.greeceri.store.models.request.ValidateAddressRequest;
import com.greeceri.store.models.response.DeliveryValidationResponse;
import com.greeceri.store.models.response.GenericResponse;
import com.greeceri.store.repositories.AddressRepository;
import com.greeceri.store.services.ShippingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingService shippingService;
    private final AddressRepository addressRepository;

    /**
     * Get available delivery slots for a given date
     */
    @GetMapping("/slots")
    public ResponseEntity<GenericResponse<List<DeliverySlot>>> getAvailableSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        if (!shippingService.isValidDeliveryDate(date)) {
            return ResponseEntity.badRequest()
                    .body(new GenericResponse<>(false,
                            "Tanggal pengiriman tidak valid. Pilih tanggal hari ini atau setelahnya.", null));
        }

        List<DeliverySlot> slots = shippingService.getAvailableSlots(date);
        return ResponseEntity.ok(new GenericResponse<>(true, "Delivery slots retrieved", slots));
    }

    /**
     * Validate if an address is within delivery area
     */
    @PostMapping("/validate-address")
    public ResponseEntity<GenericResponse<DeliveryValidationResponse>> validateAddress(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @RequestBody ValidateAddressRequest request) {

        User currentUser = (User) currentUserDetails;

        // Find address by ID and verify ownership
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.badRequest()
                    .body(new GenericResponse<>(false, "Alamat tidak ditemukan", null));
        }

        DeliveryValidationResponse result = shippingService.validateDeliveryAddress(address);
        return ResponseEntity.ok(new GenericResponse<>(true, "Address validation completed", result));
    }

    /**
     * Get shipping cost estimate based on coordinates
     */
    @GetMapping("/estimate-cost")
    public ResponseEntity<GenericResponse<DeliveryValidationResponse>> estimateCost(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {

        // Create temporary address object for validation
        Address tempAddress = Address.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build();

        DeliveryValidationResponse result = shippingService.validateDeliveryAddress(tempAddress);
        return ResponseEntity.ok(new GenericResponse<>(true, "Cost estimation completed", result));
    }
}
