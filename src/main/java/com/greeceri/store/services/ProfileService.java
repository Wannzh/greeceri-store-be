package com.greeceri.store.services;

import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.request.UpdateProfileRequest;
import com.greeceri.store.models.response.UserProfileResponse;

public interface ProfileService {
    UserProfileResponse getProfile(User currentUser);
    UserProfileResponse updateProfile(User currentUser, UpdateProfileRequest request);
}
