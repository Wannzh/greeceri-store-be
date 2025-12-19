package com.greeceri.store.services.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.greeceri.store.services.AddressValidationService;

@Service
public class AddressValidationServiceImpl implements AddressValidationService {

    // Valid cities/districts in Bandung area (case insensitive)
    private static final Set<String> VALID_CITIES = new HashSet<>(Arrays.asList(
            // Kota Bandung
            "bandung",
            "kota bandung",

            // Kabupaten Bandung
            "kabupaten bandung",
            "kab. bandung",
            "kab bandung",

            // Kabupaten Bandung Barat
            "bandung barat",
            "kabupaten bandung barat",
            "kab. bandung barat",
            "kab bandung barat",

            // Kota Cimahi
            "cimahi",
            "kota cimahi",

            // Kecamatan di Bandung (untuk fleksibilitas)
            "sukajadi",
            "sukasari",
            "cicendo",
            "andir",
            "coblong",
            "bandung wetan",
            "sumur bandung",
            "cibeunying kaler",
            "cibeunying kidul",
            "cidadap",
            "astanaanyar",
            "bojongloa kaler",
            "bojongloa kidul",
            "babakan ciparay",
            "bandung kulon",
            "regol",
            "lengkong",
            "batununggal",
            "kiaracondong",
            "antapani",
            "mandalajati",
            "arcamanik",
            "ujungberung",
            "cibiru",
            "panyileukan",
            "cinambo",
            "gedebage",
            "rancasari",
            "buahbatu",
            "margacinta",

            // Tambahan daerah sekitar
            "sukaraja",
            "dakota",
            "dayeuhkolot",
            "baleendah",
            "bojongsoang",
            "margahayu",
            "margaasih",
            "katapang",
            "soreang",
            "ciparay",
            "majalaya",
            "cileunyi",
            "rancaekek",
            "cimenyan",
            "cilengkrang"));

    // Valid postal code range for Bandung area (40xxx)
    private static final int POSTAL_CODE_MIN = 40100;
    private static final int POSTAL_CODE_MAX = 40699;

    @Override
    public boolean isValidCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            return false;
        }
        return VALID_CITIES.contains(city.toLowerCase().trim());
    }

    @Override
    public boolean isValidPostalCode(String postalCode) {
        if (postalCode == null || postalCode.trim().isEmpty()) {
            return false;
        }

        // Remove any spaces or dashes
        String cleanCode = postalCode.replaceAll("[\\s\\-]", "");

        // Check if it's a 5-digit number
        if (!cleanCode.matches("\\d{5}")) {
            return false;
        }

        // Check if it starts with valid prefix (40)
        if (!cleanCode.startsWith("40")) {
            return false;
        }

        // Check if it's within valid range
        try {
            int code = Integer.parseInt(cleanCode);
            return code >= POSTAL_CODE_MIN && code <= POSTAL_CODE_MAX;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void validateAddress(String city, String postalCode) {
        if (!isValidCity(city)) {
            throw new IllegalArgumentException(
                    "Kota/Kabupaten tidak valid. Pengiriman hanya tersedia untuk area Bandung dan sekitarnya.");
        }

        if (!isValidPostalCode(postalCode)) {
            throw new IllegalArgumentException(
                    "Kode pos tidak valid. Kode pos harus 5 digit dan dimulai dengan 40 (area Bandung).");
        }
    }
}
