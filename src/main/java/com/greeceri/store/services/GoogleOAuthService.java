package com.greeceri.store.services;

import com.greeceri.store.models.request.GoogleOAuthRequest;
import com.greeceri.store.models.response.AuthenticationResponse;

public interface GoogleOAuthService {

    /**
     * Authenticate user with Google ID token
     * Verifies token, creates user if not exists, returns JWT
     */
    AuthenticationResponse authenticateWithGoogle(GoogleOAuthRequest request);
}
