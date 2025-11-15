package com.greeceri.store.models.enums;

public enum OrderStatus {
    PENDING_PAYMENT, // Menunggu pembayaran
    PAID,            // Sudah dibayar
    SHIPPED,         // Dikirim
    DELIVERED,       // Diterima
    CANCELLED        // Dibatalkan
}
