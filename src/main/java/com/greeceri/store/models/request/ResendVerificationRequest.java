package com.greeceri.store.models.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResendVerificationRequest {
    private String email;
}
