package com.greeceri.store.services;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.greeceri.store.models.enums.Role;
import com.greeceri.store.models.request.UpdateUserStatusRequest;
import com.greeceri.store.models.response.AdminUserDetailResponse;
import com.greeceri.store.models.response.AdminUserSummaryResponse;

public interface AdminUserService {
    Page<AdminUserSummaryResponse> getUsers(int page, int size, Role role, String keyword);

    AdminUserDetailResponse getUserById(String userId);

    Map<String, Object> updateUserStatus(String userId, UpdateUserStatusRequest request);
}
