package com.greeceri.store.models.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.greeceri.store.models.enums.DeliverySlot;
import com.greeceri.store.models.enums.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // Relasi ke User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // Relasi ke Alamat (alamat pengiriman)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address shippingAddress;

    // Daftar barang di dalam pesanan
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, // Jika Order dihapus, Item ikut terhapus
            orphanRemoval = true)
    @lombok.Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @CreationTimestamp // Otomatis diisi saat dibuat
    private LocalDateTime orderDate;

    // ID Transaksi dari Xendit (misal: Invoice ID)
    private String xenditInvoiceId;

    // URL pembayaran dari Xendit
    private String paymentUrl;

    // === DELIVERY FIELDS ===

    // Tanggal pengiriman yang dipilih user
    private LocalDate deliveryDate;

    // Slot waktu pengiriman (MORNING / AFTERNOON)
    @Enumerated(EnumType.STRING)
    private DeliverySlot deliverySlot;

    // Ongkos kirim (dihitung berdasarkan jarak)
    private Double shippingCost;

    // Jarak dari store ke alamat pengiriman (dalam km)
    private Double distanceKm;
}
