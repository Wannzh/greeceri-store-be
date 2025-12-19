package com.greeceri.store.services;

public interface AddressValidationService {

    /**
     * Validate if city name is valid (within Bandung area)
     */
    boolean isValidCity(String city);

    /**
     * Validate if postal code is valid for Bandung area
     */
    boolean isValidPostalCode(String postalCode);

    /**
     * Validate city and postal code combination
     */
    void validateAddress(String city, String postalCode);
}
