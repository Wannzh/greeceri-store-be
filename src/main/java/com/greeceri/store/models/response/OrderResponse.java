package com.greeceri.store.models.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.greeceri.store.models.enums.DeliverySlot;
import com.greeceri.store.models.enums.OrderStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
    private String orderId;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private Double subtotal; // Harga barang saja
    private Double shippingCost; // Ongkos kirim
    private Double serviceFee; // Biaya layanan
    private Double totalPrice; // Grand total
    private Double distanceKm; // Jarak pengiriman
    private LocalDate deliveryDate; // Tanggal pengiriman
    private DeliverySlot deliverySlot; // Slot waktu pengiriman
    private AddressResponse shippingAddress;
    private List<OrderItemResponse> items;
    private String paymentUrl; // invoice
}
