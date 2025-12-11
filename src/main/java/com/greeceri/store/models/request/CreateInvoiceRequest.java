package com.greeceri.store.models.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateInvoiceRequest {
    @NotBlank(message = "External ID (Order ID) is required")
    private String externalId;

    @Min(value = 1, message = "Amount must be greater than 0")
    private Number amount;
}
