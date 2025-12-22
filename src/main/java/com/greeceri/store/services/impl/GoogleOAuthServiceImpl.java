package com.greeceri.store.services.impl;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.enums.Role;
import com.greeceri.store.models.request.GoogleOAuthRequest;
import com.greeceri.store.models.response.AuthenticationResponse;
import com.greeceri.store.repositories.UserRepository;
import com.greeceri.store.services.GoogleOAuthService;
import com.greeceri.store.services.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleOAuthServiceImpl implements GoogleOAuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${google.client.id}")
    private String googleClientId;

    @Override
    @Transactional
    public AuthenticationResponse authenticateWithGoogle(GoogleOAuthRequest request) {
        // Verify Google ID token
        GoogleIdToken.Payload payload = verifyGoogleToken(request.getIdToken());

        String googleId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");

        // Find or create user
        User user = findOrCreateGoogleUser(googleId, email, name, pictureUrl);

        // Generate JWT tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    private GoogleIdToken.Payload verifyGoogleToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                throw new RuntimeException("Invalid Google ID token");
            }

            return idToken.getPayload();
        } catch (Exception e) {
            log.error("Failed to verify Google token: {}", e.getMessage());
            throw new RuntimeException("Failed to verify Google token: " + e.getMessage());
        }
    }

    private User findOrCreateGoogleUser(String googleId, String email, String name, String pictureUrl) {
        // First try to find by Google ID
        Optional<User> userByGoogleId = userRepository.findByGoogleId(googleId);
        if (userByGoogleId.isPresent()) {
            User existingUser = userByGoogleId.get();
            // Update profile image if changed
            if (pictureUrl != null && !pictureUrl.equals(existingUser.getProfileImageUrl())) {
                existingUser.setProfileImageUrl(pictureUrl);
                userRepository.save(existingUser);
            }
            return existingUser;
        }

        // Check if user exists by email (registered via normal registration)
        Optional<User> userByEmail = userRepository.findByEmail(email);
        if (userByEmail.isPresent()) {
            User existingUser = userByEmail.get();
            // Link Google account to existing user
            existingUser.setGoogleId(googleId);
            if (pictureUrl != null) {
                existingUser.setProfileImageUrl(pictureUrl);
            }
            // Enable user if not already enabled (skip email verification)
            existingUser.setEnabled(true);
            return userRepository.save(existingUser);
        }

        // Create new user
        User newUser = User.builder()
                .googleId(googleId)
                .email(email)
                .name(name)
                .profileImageUrl(pictureUrl)
                .password(passwordEncoder.encode(UUID.randomUUID().toString())) // Random password
                .role(Role.USER)
                .enabled(true) // Google users are auto-verified
                .build();

        return userRepository.save(newUser);
    }
}
