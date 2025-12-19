package com.greeceri.store.models.enums;

public enum OrderStatus {
    PENDING_PAYMENT, // Menunggu pembayaran
    PAID, // Sudah dibayar
    PROCESSING, // Sedang disiapkan (NEW)
    SHIPPED, // Dikirim
    DELIVERED, // Diterima
    CANCELLED; // Dibatalkan

    public static boolean isValidTransition(OrderStatus from, OrderStatus to) {
        return switch (from) {
            case PENDING_PAYMENT -> to == PAID || to == CANCELLED;
            case PAID -> to == PROCESSING || to == CANCELLED;
            case PROCESSING -> to == SHIPPED || to == CANCELLED;
            case SHIPPED -> to == DELIVERED;
            default -> false;
        };
    }
}
