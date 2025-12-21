package com.greeceri.store.controllers.payment;

import com.greeceri.store.models.entity.Order;
import com.greeceri.store.models.enums.OrderStatus;
import com.greeceri.store.models.request.CreateInvoiceRequest;
import com.greeceri.store.models.response.GeneralResponse;
import com.greeceri.store.models.response.GenericResponse;
import com.greeceri.store.repositories.OrderRepository;
import com.xendit.exception.XenditException;
import com.xendit.model.Invoice;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final OrderRepository orderRepository;

    @Value("${xendit.callback.token}")
    private String xenditCallbackToken;

    @PostMapping("/create-invoice")
    public ResponseEntity<GenericResponse<Map<String, String>>> createInvoice(
            @Valid @RequestBody CreateInvoiceRequest request) {
        try {
            Map<String, Object> params = new HashMap<>();

            params.put("external_id", request.getExternalId());
            params.put("amount", request.getAmount());
            params.put("payer_email", "tes.email@gmail.com"); // Email customer
            params.put("description", "Pembayaran untuk " + request.getExternalId());

            // Membuat invoice di Xendit
            Invoice invoice = Invoice.create(params);

            // Yang Anda perlukan adalah invoice_url
            String invoiceUrl = invoice.getInvoiceUrl();

            Map<String, String> responseData = new HashMap<>();
            responseData.put("invoice_url", invoiceUrl);

            return ResponseEntity.ok(new GenericResponse<>(true, "Invoice created successfully", responseData));

        } catch (XenditException e) {
            throw new RuntimeException("Failed to create invoice: " + e.getMessage());
        }
    }

    @PostMapping("/callback")
    public ResponseEntity<GeneralResponse> paymentCallback(@RequestBody Map<String, Object> body,
            @RequestHeader(value = "x-callback-token", required = false) String callbackToken) {

        if (callbackToken == null || !callbackToken.equals(xenditCallbackToken)) {
            throw new RuntimeException("Invalid callback token");
        }

        try {
            String externalId = (String) body.get("external_id"); // Ini adalah Order ID kita
            String status = (String) body.get("status");

            // 3. Cari Order di database
            Order order = orderRepository.findById(externalId)
                    .orElseThrow(() -> new RuntimeException("Order not found: " + externalId));

            // 4. Update status pesanan
            if ("PAID".equals(status) || "SETTLED".equals(status)) {
                if (order.getStatus() == OrderStatus.PENDING_PAYMENT) {
                    order.setStatus(OrderStatus.PAID);
                    orderRepository.save(order);
                }
            } else if ("EXPIRED".equals(status)) {
                // (Opsional: Handle jika invoice kedaluwarsa)
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
            }
            
            // 5. Kembalikan 200 OK agar Xendit tahu
            return ResponseEntity.ok(new GeneralResponse(true, "Payment callback processed successfully"));

        } catch (Exception e) {
            // Tangani error jika data tidak valid
            throw new RuntimeException("Error processing callback: " + e.getMessage());
        }
    }
}
