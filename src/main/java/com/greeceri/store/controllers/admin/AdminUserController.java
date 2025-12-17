package com.greeceri.store.controllers.admin;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greeceri.store.models.enums.Role;
import com.greeceri.store.models.request.UpdateUserStatusRequest;
import com.greeceri.store.models.response.AdminUserDetailResponse;
import com.greeceri.store.models.response.AdminUserSummaryResponse;
import com.greeceri.store.models.response.GenericResponse;
import com.greeceri.store.services.AdminUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    // GET /api/admin/users - List all users with pagination, sorting, and search
    @GetMapping
    public ResponseEntity<GenericResponse<Page<AdminUserSummaryResponse>>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String keyword) {

        Role parsedRole = null;

        if (role != null && !role.isBlank()) {
            try {
                parsedRole = Role.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid role: " + role + ". Valid values are: ADMIN, USER");
            }
        }

        Page<AdminUserSummaryResponse> users = adminUserService.getUsers(page, size, parsedRole, keyword);

        return ResponseEntity.ok(
                new GenericResponse<>(true, "Users retrieved successfully", users));
    }

    // GET /api/admin/users/{userId} - Get user detail
    @GetMapping("/{userId}")
    public ResponseEntity<GenericResponse<AdminUserDetailResponse>> getUserById(@PathVariable String userId) {
        AdminUserDetailResponse userDetail = adminUserService.getUserById(userId);
        return ResponseEntity.ok(
                new GenericResponse<>(true, "User detail retrieved successfully", userDetail));
    }

    // PATCH /api/admin/users/{userId}/status - Enable/Disable user
    @PatchMapping("/{userId}/status")
    public ResponseEntity<GenericResponse<Map<String, Object>>> updateUserStatus(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserStatusRequest request) {

        Map<String, Object> result = adminUserService.updateUserStatus(userId, request);
        return ResponseEntity.ok(
                new GenericResponse<>(true, (String) result.get("message"), result));
    }
}
