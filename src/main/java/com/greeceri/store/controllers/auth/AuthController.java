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
import com.greeceri.store.models.request.LoginRequest;
import com.greeceri.store.models.request.RegisterRequest;
import com.greeceri.store.models.request.ResendVerificationRequest;
import com.greeceri.store.models.request.ResetPasswordRequest;
import com.greeceri.store.models.response.AuthenticationResponse;
import com.greeceri.store.repositories.UserRepository;
import com.greeceri.store.services.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @Value("${app.verification.redirect.success}")
    private String successRedirectUrl;

    @Value("${app.verification.redirect.failure}")
    private String failureRedirectUrl;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        AuthenticationResponse response = authenticationService.register(request);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        AuthenticationResponse response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<AuthenticationResponse> resendVerificationEmail(
            @RequestBody ResendVerificationRequest request) {
        AuthenticationResponse response = authenticationService.resendVerificationEmail(request);
        return ResponseEntity.ok(response);
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
    public ResponseEntity<AuthenticationResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        AuthenticationResponse response = authenticationService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AuthenticationResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        AuthenticationResponse response = authenticationService.resetPassword(request);
        return ResponseEntity.ok(response);
    }
}
