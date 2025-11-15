package com.greeceri.store.models.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "carts")
public class Cart {
    @Id
    // @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Meminjam Id User sebagai Primary Key
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    // Satu Cart punya banyak Item
    @OneToMany(
        mappedBy = "cart",
        cascade = CascadeType.ALL, // Menghapus CartItem jika Cart di hapus
        orphanRemoval = true // Hapus CartItem dari List dan DB
    )
    private List<CartItem> items = new ArrayList<>();

    // Helper Method sinkronisasi
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
    }

    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
    }

}
