package com.greeceri.store.models.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse {
    private String id;
    private String label;
    private String receiverName;
    private String phoneNumber;
    private String fullAddress;
    private String city;
    private String postalCode;
    private boolean isMainAddress;
}
