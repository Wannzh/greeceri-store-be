package com.greeceri.store.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.greeceri.store.models.entity.Address;
import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.request.AddressRequest;
import com.greeceri.store.models.response.AddressResponse;
import com.greeceri.store.repositories.AddressRepository;
import com.greeceri.store.services.AddressService;
import com.greeceri.store.services.AddressValidationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final AddressValidationService addressValidationService;

    @Override
    public List<AddressResponse> getAllAddressesForUser(User currentUser) {
        return addressRepository.findByUser(currentUser)
                .stream()
                .map(this::mapAddressToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AddressResponse getAddressById(User currentUser, String addressId) {
        Address address = findAndValidateOwnership(currentUser, addressId);

        return mapAddressToResponse(address);
    }

    @Override
    @Transactional
    public AddressResponse addAddress(User currentUser, AddressRequest request) {
        // Validate city and postal code
        addressValidationService.validateAddress(request.getCity(), request.getPostalCode());

        Address newAddress = Address.builder()
                .user(currentUser)
                .label(request.getLabel())
                .receiverName(request.getReceiverName())
                .phoneNumber(request.getPhoneNumber())
                .fullAddress(request.getFullAddress())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();

        List<Address> userAddress = addressRepository.findByUser(currentUser);
        if (userAddress.isEmpty()) {
            newAddress.setMainAddress(true);
        } else {
            newAddress.setMainAddress(false);
        }

        Address saveAddress = addressRepository.save(newAddress);

        return mapAddressToResponse(saveAddress);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(User currentUser, String addressId, AddressRequest request) {
        // Validate city and postal code
        addressValidationService.validateAddress(request.getCity(), request.getPostalCode());

        Address address = findAndValidateOwnership(currentUser, addressId);

        address.setLabel(request.getLabel());
        address.setReceiverName(request.getReceiverName());
        address.setPhoneNumber(request.getPhoneNumber());
        address.setFullAddress(request.getFullAddress());
        address.setCity(request.getCity());
        address.setPostalCode(request.getPostalCode());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());

        Address updatedAddress = addressRepository.save(address);
        return mapAddressToResponse(updatedAddress);

    }

    @Override
    @Transactional
    public void deleteAddress(User currentUser, String id) {
        Address address = findAndValidateOwnership(currentUser, id);

        // Validasi jika alamat utama
        if (address.isMainAddress()) {
            // Coba cari alamat lain untuk dijadikan alamat utama baru
            Optional<Address> anyOtherAddress = addressRepository.findByUser(currentUser)
                    .stream()
                    .filter(a -> !a.getId().equals(id)) // Cari yang BUKAN alamat ini
                    .findFirst(); // Ambil yang pertama

            // Jika ada alamat lain, jadikan itu sebagai utama
            anyOtherAddress.ifPresent(newMain -> {
                newMain.setMainAddress(true);
                addressRepository.save(newMain);
            });
        }

        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public List<AddressResponse> setMainAddress(User currentUser, String id) {
        Address newMainAddress = findAndValidateOwnership(currentUser, id);

        Optional<Address> oldMainAddressOpt = addressRepository.findByUserAndIsMainAddress(currentUser, true);

        if (oldMainAddressOpt.isPresent()) {
            Address oldMainAddress = oldMainAddressOpt.get();

            if (!oldMainAddress.getId().equals(id)) { // Pastikan itu bukan alamat yang sama
                oldMainAddress.setMainAddress(false);
                addressRepository.save(oldMainAddress);
            }
        }

        newMainAddress.setMainAddress(true);
        addressRepository.save(newMainAddress);

        return getAllAddressesForUser(currentUser);
    }

    private AddressResponse mapAddressToResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .label(address.getLabel())
                .receiverName(address.getReceiverName())
                .phoneNumber(address.getPhoneNumber())
                .fullAddress(address.getFullAddress())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .isMainAddress(address.isMainAddress())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .build();
    }

    private Address findAndValidateOwnership(User user, String addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access Denied!");
        }

        return address;
    }

}
