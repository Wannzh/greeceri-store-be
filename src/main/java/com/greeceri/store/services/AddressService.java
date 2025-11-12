package com.greeceri.store.services;

import java.util.List;

import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.request.AddressRequest;
import com.greeceri.store.models.response.AddressResponse;

public interface AddressService {
    List<AddressResponse> getAllAddressesForUser(User currentUser);
    AddressResponse addAddress(User currentUser, AddressRequest request);
    AddressResponse updateAddress(User currentUser, String id,AddressRequest request);
    void deleteAddress(User currentUser, String id);
    List<AddressResponse> setMainAddress(User currentUser, String id);
}
