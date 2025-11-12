package com.greeceri.store.controllers;

import com.xendit.exception.XenditException;
import com.xendit.model.Invoice;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Value("${xendit.callback.token}")
    private String myCallbackToken;

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
    public String paymentCallback(@RequestBody Map<String, Object> body,
            @RequestHeader(value = "x-callback-token", required = false) String callbackToken) {

        // 1. Validasi Callback Token (PENTING UNTUK KEAMANAN)
        // Bandingkan "callbackToken" dari header dengan token yang Anda set di
        // Dashboard Xendit
        String myToken = "RAHASIA_DARI_DASHBOARD_XENDIT"; // Ambil dari env/properties

        if (!myToken.equals(callbackToken)) {
            System.out.println("Callback Token Tidak Valid!");
            // return 403 Forbidden atau sejenisnya
            return "Error";
        }

        // 2. Proses datanya
        System.out.println("Menerima Callback dari Xendit:");
        System.out.println(body);

        String externalId = (String) body.get("external_id");
        String status = (String) body.get("status");

        if ("PAID".equals(status)) {
            // Logika bisnis Anda
            // Contoh: panggil service untuk update status order di database Anda
            // UPDATE orders SET status = 'LUNAS' WHERE id = externalId;
            System.out.println("Order " + externalId + " telah LUNAS.");
        }

        // Balas dengan HTTP 200 OK agar Xendit tahu callback diterima
        return "OK";
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
