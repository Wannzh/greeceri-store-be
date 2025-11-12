package com.greeceri.store.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequest {
    private String label;
    private String receiverName;
    private String phoneNumber;
    private String fullAddress;
    private String city;
    private String postalCode;
}
