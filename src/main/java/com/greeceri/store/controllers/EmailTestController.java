package com.greeceri.store.controllers;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailTestController {

    // private final EmailService emailService;

    // // Menggunakan constructor injection (best practice)
    // public EmailTestController(EmailService emailService) {
    //     this.emailService = emailService;
    // }

    // @GetMapping("/test-email")
    // public ResponseEntity<String> sendTestEmail(@RequestParam("to") String toEmail) {
    //     try {
    //         emailService.sendTestEmail(toEmail);
    //         return ResponseEntity.ok("Permintaan pengiriman email ke " + toEmail + " berhasil. Cek konsol untuk response dari SendGrid.");
    //     } catch (Exception e) {
    //         return ResponseEntity.internalServerError().body("Gagal memicu pengiriman email: " + e.getMessage());
    //     }
    // }
}