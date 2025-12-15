package com.greeceri.store.controllers.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.request.AddressRequest;
import com.greeceri.store.models.request.UpdateProfileRequest;
import com.greeceri.store.models.response.AddressResponse;
import com.greeceri.store.models.response.GenericResponse;
import com.greeceri.store.models.response.GeneralResponse;
import com.greeceri.store.models.response.UserProfileResponse;
import com.greeceri.store.services.AddressService;
import com.greeceri.store.services.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final AddressService addressService;

    @GetMapping("/profile")
    public ResponseEntity<GenericResponse<UserProfileResponse>> getAllMyProfile(
            @AuthenticationPrincipal UserDetails currentUserDetails) {
        User currentUser = (User) currentUserDetails;
        UserProfileResponse profileData = profileService.getProfile(currentUser);
        return ResponseEntity.ok(new GenericResponse<>(true, "Profile retrieved successfully", profileData));
    }

    @PutMapping("/profile")
    public ResponseEntity<GenericResponse<UserProfileResponse>> updateMyProfile(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        User currentUser = (User) currentUserDetails;
        UserProfileResponse updatedProfile = profileService.updateProfile(currentUser, request);
        return ResponseEntity.ok(new GenericResponse<>(true, "Profile updated successfully", updatedProfile));
    }

    @GetMapping("/address")
    public ResponseEntity<GenericResponse<List<AddressResponse>>> getAllMyAddresses(
            @AuthenticationPrincipal UserDetails currentUserDetails) {
        User currentUser = (User) currentUserDetails;
        List<AddressResponse> addresses = addressService.getAllAddressesForUser(currentUser);
        return ResponseEntity.ok(new GenericResponse<>(true, "Addresses retrieved successfully", addresses));
    }

    @GetMapping("/address/{addressId}")
    public ResponseEntity<GenericResponse<AddressResponse>> getAddressById(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable String addressId) {
        
        User currentUser = (User) currentUserDetails;
        
        AddressResponse response = addressService.getAddressById(currentUser, addressId);

        return ResponseEntity.ok(new GenericResponse<>(true, "Success retrieving address detail", response));
    }

    @PostMapping("/address")
    public ResponseEntity<GenericResponse<AddressResponse>> addNewAddress(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @Valid @RequestBody AddressRequest request) {
        User currentUser = (User) currentUserDetails;
        AddressResponse newAddress = addressService.addAddress(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponse<>(true, "Address added successfully", newAddress));
    }

    @PutMapping("/address/{addressId}")
    public ResponseEntity<GenericResponse<AddressResponse>> updateMyAddress(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable String addressId,
            @Valid @RequestBody AddressRequest request) {
        User currentUser = (User) currentUserDetails;
        AddressResponse updatedAddress = addressService.updateAddress(currentUser, addressId, request);
        return ResponseEntity.ok(new GenericResponse<>(true, "Address updated successfully", updatedAddress));
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<GeneralResponse> deleteMyAddress(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable String addressId) {
        User currentUser = (User) currentUserDetails;
        addressService.deleteAddress(currentUser, addressId);
        return ResponseEntity.ok(new GeneralResponse(true, "Address deleted successfully"));
    }

    @PutMapping("/address/{addressId}/set-main")
    public ResponseEntity<GenericResponse<List<AddressResponse>>> setMainAddress(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable String addressId) {
        User currentUser = (User) currentUserDetails;
        List<AddressResponse> updatedAddresses = addressService.setMainAddress(currentUser, addressId);
        return ResponseEntity.ok(new GenericResponse<>(true, "Main address updated successfully", updatedAddresses));
    }

}
