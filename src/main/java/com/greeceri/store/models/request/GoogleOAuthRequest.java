package com.greeceri.store.models.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleOAuthRequest {

    @NotBlank(message = "ID Token is required")
    private String idToken;
}
