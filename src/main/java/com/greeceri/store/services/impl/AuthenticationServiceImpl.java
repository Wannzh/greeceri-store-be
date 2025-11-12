package com.greeceri.store.services.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.greeceri.store.models.entity.PasswordResetToken;
import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.enums.Role;
import com.greeceri.store.models.request.ForgotPasswordRequest;
import com.greeceri.store.models.request.LoginRequest;
import com.greeceri.store.models.request.RegisterRequest;
import com.greeceri.store.models.request.ResendVerificationRequest;
import com.greeceri.store.models.request.ResetPasswordRequest;
import com.greeceri.store.models.response.AuthenticationResponse;
import com.greeceri.store.repositories.PasswordResetTokenRepository;
import com.greeceri.store.repositories.UserRepository;
import com.greeceri.store.services.AuthenticationService;
import com.greeceri.store.services.EmailService;
import com.greeceri.store.services.JwtService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final PasswordResetTokenRepository tokenRepository;

    @Value("${app.base.url}")
    private String appBaseUrl;

    @Value("${app.logo.url}")
    private String appLogoUrl;

    @Override
    public AuthenticationResponse register(RegisterRequest request) {

        String verificationToken = UUID.randomUUID().toString();

        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .verificationToken(verificationToken)
                .enabled(false)
                .verificationTokenExpiry(LocalDateTime.now().plusMinutes(5))
                .build();

        userRepository.save(user);

        sendVerificationEmail(user, verificationToken);

        return AuthenticationResponse.builder()
                .token("Registration successful. Please verify your email to activate your account.")
                .build();
    }

    @Override
    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponse resendVerificationEmail(ResendVerificationRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isPresent() && !userOptional.get().isEnabled()) {
            User user = userOptional.get();
            String newVerificationToken = UUID.randomUUID().toString();
            LocalDateTime newExpiryTime = LocalDateTime.now().plusMinutes(5);
            user.setVerificationToken(newVerificationToken);
            user.setVerificationTokenExpiry(newExpiryTime);
            userRepository.save(user);

            sendVerificationEmail(user, newVerificationToken);

            return AuthenticationResponse.builder()
                    .token("Verification email resent. Please check your inbox.")
                    .build();
        } else {
            return AuthenticationResponse.builder()
                    .token("User not found or already verified.")
                    .build();
        }
    }

    @Override
    public AuthenticationResponse forgotPassword(ForgotPasswordRequest request) {
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            String token = UUID.randomUUID().toString();
            PasswordResetToken passwordResetToken = new PasswordResetToken(token, user);
            tokenRepository.save(passwordResetToken);

            sendPasswordResetEmail(user, token);
        }

        return AuthenticationResponse.builder()
                .token("If the email is registered, a password reset link has been sent.")
                .build();
    }

    @Override
    public AuthenticationResponse resetPassword(ResetPasswordRequest request) {
        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(request.getToken());

        if (tokenOptional.isEmpty()) {
            throw new RuntimeException("Invalid password reset token.");
        }

        PasswordResetToken resetToken = tokenOptional.get();

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("Password reset token has expired.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        tokenRepository.delete(resetToken);

        return AuthenticationResponse.builder()
                .token("Password has been successfully reset.")
                .build();
    }

    private void sendVerificationEmail(User user, String token) {
        String verificationLink = appBaseUrl + "/api/auth/verify?token=" + token;

        Context context = new Context();
        context.setVariable("name", user.getName());
        context.setVariable("logoUrl", appLogoUrl);
        context.setVariable("verificationLink", verificationLink);

        String htmlBody = templateEngine.process("verification-email", context);

        String subject = "Welcome! Verify Your Greeceri Store Account";

        // Gunakan EmailService (interface) untuk mengirim
        emailService.sendEmail(user.getEmail(), subject, htmlBody);
    }

    private void sendPasswordResetEmail(User user, String token) {
        String resetLink = appBaseUrl + "/reset-password?token=" + token;

        Context context = new Context();
        context.setVariable("name", user.getName());
        context.setVariable("logoUrl", appLogoUrl);
        context.setVariable("resetLink", resetLink);

        String htmlBody = templateEngine.process("password-reset-email", context);

        String subject = "Greeceri Store Password Reset Request";

        // Gunakan EmailService (interface) untuk mengirim
        emailService.sendEmail(user.getEmail(), subject, htmlBody); 
    }

}
