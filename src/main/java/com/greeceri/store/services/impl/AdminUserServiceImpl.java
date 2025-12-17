package com.greeceri.store.services.impl;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.enums.Role;
import com.greeceri.store.models.request.UpdateUserStatusRequest;
import com.greeceri.store.models.response.AdminUserDetailResponse;
import com.greeceri.store.models.response.AdminUserSummaryResponse;
import com.greeceri.store.repositories.UserRepository;
import com.greeceri.store.services.AdminUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;

    @Override
    public Page<AdminUserSummaryResponse> getUsers(int page, int size, Role roleEnum, String keyword) {
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        Pageable pageable = PageRequest.of(page, size);
        String roleStr = (roleEnum != null) ? roleEnum.name() : null;

        Page<User> userPage = userRepository.findAllByRoleAndKeyword(roleStr, keyword, pageable);

        return userPage.map(this::mapToSummaryResponse);
    }

    @Override
    public AdminUserDetailResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Long orderCount = userRepository.countOrdersByUserId(userId);
        Double totalSpent = userRepository.sumTotalSpentByUserId(userId);

        return AdminUserDetailResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .createdAt(user.getVerificationTokenExpiry())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .orderCount(orderCount)
                .totalSpent(totalSpent)
                .build();
    }

    @Override
    @Transactional
    public Map<String, Object> updateUserStatus(String userId, UpdateUserStatusRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        user.setEnabled(request.getEnabled());
        userRepository.save(user);

        String statusText = request.getEnabled() ? "enabled" : "disabled";

        return Map.of(
                "success", true,
                "message", "User " + user.getName() + " has been " + statusText,
                "enabled", request.getEnabled());
    }

    private AdminUserSummaryResponse mapToSummaryResponse(User user) {
        Long totalOrders = userRepository.countOrdersByUserId(user.getId());

        return AdminUserSummaryResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .createdAt(user.getVerificationTokenExpiry())
                .totalOrders(totalOrders)
                .build();
    }
}
