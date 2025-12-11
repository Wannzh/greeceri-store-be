package com.greeceri.store.services;

import com.greeceri.store.models.request.ForgotPasswordRequest;
import com.greeceri.store.models.request.LoginRequest;
import com.greeceri.store.models.request.RegisterRequest;
import com.greeceri.store.models.request.ResendVerificationRequest;
import com.greeceri.store.models.request.ResetPasswordRequest;
import com.greeceri.store.models.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse login(LoginRequest request);
    void resendVerificationEmail(ResendVerificationRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
}
