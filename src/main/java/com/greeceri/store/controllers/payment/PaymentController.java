package com.greeceri.store.controllers.payment;

import com.greeceri.store.models.entity.Order;
import com.greeceri.store.models.enums.OrderStatus;
import com.greeceri.store.repositories.OrderRepository;
import com.xendit.exception.XenditException;
import com.xendit.model.Invoice;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    public Map<String, String> createInvoice(@RequestBody CreateInvoiceRequest request) {
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

            Map<String, String> response = new HashMap<>();
            response.put("invoice_url", invoiceUrl);

            return response;

        } catch (XenditException e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return errorResponse;
        }
    }

    @PostMapping("/callback")
    public ResponseEntity<String> paymentCallback(@RequestBody Map<String, Object> body,
            @RequestHeader(value = "x-callback-token", required = false) String callbackToken) {

        if (callbackToken == null || !callbackToken.equals(xenditCallbackToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid callback token");
        }

        try {
            String externalId = (String) body.get("external_id"); // Ini adalah Order ID kita
            String status = (String) body.get("status");

            // 3. Cari Order di database
            Order order = orderRepository.findById(externalId)
                    .orElseThrow(() -> new RuntimeException("Pesanan tidak ditemukan: " + externalId));

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
            return ResponseEntity.ok("Callback received successfully");

        } catch (Exception e) {
            // Tangani error jika data tidak valid
            return ResponseEntity.badRequest().body("Error processing callback: " + e.getMessage());
        }
    }
}

// Buat class DTO (Data Transfer Object) sederhana untuk menampung request body
class CreateInvoiceRequest {
    private String externalId;
    private Number amount;

    // Tambahkan Getter dan Setter
    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Number getAmount() {
        return amount;
    }

    public void setAmount(Number amount) {
        this.amount = amount;
    }
}
