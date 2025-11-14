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
import com.greeceri.store.models.response.UserProfileResponse;
import com.greeceri.store.services.AddressService;
import com.greeceri.store.services.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final AddressService addressService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getAllMyProfile(
            @AuthenticationPrincipal UserDetails currentUserDetails) {
        User currentUser = (User) currentUserDetails;
        return ResponseEntity.ok(profileService.getProfile(currentUser));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateMyProfile(@AuthenticationPrincipal UserDetails currentUserDetails,
            @RequestBody UpdateProfileRequest request) {
        User currentUser = (User) currentUserDetails;

        return ResponseEntity.ok(profileService.updateProfile(currentUser, request));
    }

    @GetMapping("/address")
    public ResponseEntity<List<AddressResponse>> getAllMyAddresses(
            @AuthenticationPrincipal UserDetails currentUserDetails) {
        User currentUser = (User) currentUserDetails;
        return ResponseEntity.ok(addressService.getAllAddressesForUser(currentUser));
    }

    @PostMapping("/address")
    public ResponseEntity<AddressResponse> addNewAddress(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @RequestBody AddressRequest request) {
        User currentUser = (User) currentUserDetails;
        // return 201 CREATED karena ini membuat resource baru
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.addAddress(currentUser, request));
    }

    @PutMapping("/address/{addressId}")
    public ResponseEntity<AddressResponse> updateMyAddress(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable String addressId,
            @RequestBody AddressRequest request) {
        User currentUser = (User) currentUserDetails;

        return ResponseEntity.ok(addressService.updateAddress(currentUser, addressId, request));
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<Void> deleteMyAddress(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable String addressId
    ) {
        User currentUser = (User) currentUserDetails;
        addressService.deleteAddress(currentUser, addressId);
        // return 204 NO CONTENT (standar untuk DELETE sukses)
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/address/{addressId}/set-main")
    public ResponseEntity<List<AddressResponse>> setMainAddress(
            @AuthenticationPrincipal UserDetails currentUserDetails,
            @PathVariable String addressId
    ) {
        User currentUser = (User) currentUserDetails;
        // Mengembalikan daftar alamat yang sudah ter-update
        return ResponseEntity.ok(addressService.setMainAddress(currentUser, addressId));
    }

}
