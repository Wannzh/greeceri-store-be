package com.greeceri.store.services.impl;

import org.springframework.stereotype.Service;

import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.request.UpdateProfileRequest;
import com.greeceri.store.models.response.UserProfileResponse;
import com.greeceri.store.repositories.UserRepository;
import com.greeceri.store.services.ProfileService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final UserRepository userRepository;

    @Override
    public UserProfileResponse getProfile(User currentUser) {
        return mapUserToProfileResponse(currentUser);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(User currentUser, UpdateProfileRequest request) {
        currentUser.setName(request.getName());
        currentUser.setPhoneNumber(request.getPhoneNumber());
        currentUser.setGender(request.getGender());
        currentUser.setDateOfBirth(request.getDateOfBirth());

        User updateUser = userRepository.save(currentUser);

        return mapUserToProfileResponse(updateUser);
    }


    private UserProfileResponse mapUserToProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .build();
    }
}
