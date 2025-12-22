package com.greeceri.store.controllers.auth;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.request.ForgotPasswordRequest;
import com.greeceri.store.models.request.GoogleOAuthRequest;
import com.greeceri.store.models.request.LoginRequest;
import com.greeceri.store.models.request.RefreshTokenRequest;
import com.greeceri.store.models.request.RegisterRequest;
import com.greeceri.store.models.request.ResendVerificationRequest;
import com.greeceri.store.models.request.ResetPasswordRequest;
import com.greeceri.store.models.response.AuthenticationResponse;
import com.greeceri.store.models.response.GeneralResponse;
import com.greeceri.store.models.response.GenericResponse;
import com.greeceri.store.repositories.UserRepository;
import com.greeceri.store.services.AuthenticationService;
import com.greeceri.store.services.GoogleOAuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final GoogleOAuthService googleOAuthService;
    private final UserRepository userRepository;

    @Value("${app.verification.redirect.success}")
    private String successRedirectUrl;

    @Value("${app.verification.redirect.failure}")
    private String failureRedirectUrl;

    @PostMapping("/register")
    public ResponseEntity<GenericResponse<AuthenticationResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthenticationResponse response = authenticationService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new GenericResponse<>(true, "Registration successful. Please check your email.", response));

    }

    @PostMapping("/login")
    public ResponseEntity<GenericResponse<AuthenticationResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthenticationResponse response = authenticationService.login(request);

        return ResponseEntity.ok(
                new GenericResponse<>(true, "Login Successful", response));
    }

    /**
     * Google OAuth Login
     * POST /api/auth/google
     */
    @PostMapping("/google")
    public ResponseEntity<GenericResponse<AuthenticationResponse>> googleLogin(
            @Valid @RequestBody GoogleOAuthRequest request) {
        AuthenticationResponse response = googleOAuthService.authenticateWithGoogle(request);
        return ResponseEntity.ok(new GenericResponse<>(true, "Google login successful", response));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<GenericResponse<AuthenticationResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        AuthenticationResponse response = authenticationService.refreshToken(request);
        return ResponseEntity.ok(new GenericResponse<>(true, "Token refreshed successfully", response));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<GeneralResponse> resendVerificationEmail(
            @RequestBody ResendVerificationRequest request) {
        authenticationService.resendVerificationEmail(request);

        return ResponseEntity.ok(new GeneralResponse(true, "Verification email resent"));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyAccount(@RequestParam("token") String token) {
        Optional<User> userOptional = userRepository.findByVerificationToken(token);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(failureRedirectUrl + "?error=invalid_token"))
                    .build();
        }

        User user = userOptional.get();

        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            // Token sudah kedaluwarsa
            // Hapus token lama agar tidak bisa dipakai lagi
            user.setVerificationToken(null);
            user.setVerificationTokenExpiry(null);
            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(failureRedirectUrl + "?error=token_expired"))
                    .build();
        }

        user.setEnabled(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(successRedirectUrl))
                .build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<GeneralResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authenticationService.forgotPassword(request);

        return ResponseEntity.ok(
                new GeneralResponse(true, "If the email is registered, a password reset link has been sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<GeneralResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(request);

        return ResponseEntity.ok(
                new GeneralResponse(true, "Password has been successfully reset."));
    }
}
