package com.greeceri.store.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.greeceri.store.models.entity.Address;
import com.greeceri.store.models.entity.Cart;
import com.greeceri.store.models.entity.CartItem;
import com.greeceri.store.models.entity.Order;
import com.greeceri.store.models.entity.OrderItem;
import com.greeceri.store.models.entity.Product;
import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.enums.OrderStatus;
import com.greeceri.store.models.request.CheckoutRequest;
import com.greeceri.store.models.response.AddressResponse;
import com.greeceri.store.models.response.OrderItemResponse;
import com.greeceri.store.models.response.OrderResponse;
import com.greeceri.store.repositories.AddressRepository;
import com.greeceri.store.repositories.CartItemRepository;
import com.greeceri.store.repositories.CartRepository;
import com.greeceri.store.repositories.OrderItemRepository;
import com.greeceri.store.repositories.OrderRepository;
import com.greeceri.store.repositories.ProductRepository;
import com.greeceri.store.services.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository; // Untuk update stok

    @Override
    @Transactional
    public OrderResponse createOrderFromCart(User currentUser, CheckoutRequest request) {
        // Ambil Keranjang User
        Cart cart = cartRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is Empty");
        }

        // Validasi Alamat Pengiriman
        Address shippingAddress = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new RuntimeException("Alamat tidak ditemukan"));
                
        // Validasi Kepemilikan Alamat
        if (!shippingAddress.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Akses ditolak. Alamat ini bukan milik Anda.");
        }
        
        // Create objek order
        Order newOrder = Order.builder()
                .user(currentUser)
                .shippingAddress(shippingAddress)
                .status(OrderStatus.PENDING_PAYMENT) // Status Awal
                .items(new ArrayList<>())
                .build();

        double grandTotal = 0.0;

        // Move Item from cart to Order
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            // Cek Stock
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Stock For " + product.getName() + " insufficient");
            }

            // Create Order
            OrderItem orderItem = OrderItem.builder()
                    .order(newOrder)
                    .productId(product.getId())
                    .productName(product.getName())
                    .priceAtPurchase(product.getPrice()) // <-- Ini snapshot harga
                    .quantity(cartItem.getQuantity())
                    .build();

            newOrder.getItems().add(orderItem);
            
            // Update Stok Produk
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            grandTotal += (product.getPrice() * cartItem.getQuantity());
        }
        newOrder.setTotalPrice(grandTotal);

        // Simpan Order (dan OrderItem-nya via cascade)
        Order savedOrder = orderRepository.save(newOrder);

        // Kosongkan Keranjang
        cartItemRepository.deleteAll(cart.getItems());

        // Kembalikan DTO
        return mapOrderToResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getMyOrders(User currentUser) {
        return orderRepository.findByUserOrderByOrderDateDesc(currentUser)
                .stream()
                .map(this::mapOrderToResponse) // Panggil mapper
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getMyOrderDetails(User currentUser, String orderId) {
        // Ambil order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pesanan tidak ditemukan"));
        
        // Validasi Kepemilikan
        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Akses ditolak. Pesanan ini bukan milik Anda.");
        }

        return mapOrderToResponse(order);
    }

    private OrderResponse mapOrderToResponse(Order order) {
        // Map OrderItems
        List<OrderItemResponse> itemResponses = order.getItems().stream().map(item -> 
            OrderItemResponse.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .priceAtPurchase(item.getPriceAtPurchase())
                    .quantity(item.getQuantity())
                    .subTotal(item.getPriceAtPurchase() * item.getQuantity())
                    .build()
        ).collect(Collectors.toList());

        // Map Alamat
        AddressResponse addressResponse = AddressResponse.builder()
                .id(order.getShippingAddress().getId())
                .label(order.getShippingAddress().getLabel())
                .receiverName(order.getShippingAddress().getReceiverName())
                .phoneNumber(order.getShippingAddress().getPhoneNumber())
                .fullAddress(order.getShippingAddress().getFullAddress())
                .city(order.getShippingAddress().getCity())
                .postalCode(order.getShippingAddress().getPostalCode())
                .isMainAddress(order.getShippingAddress().isMainAddress())
                .build();
                
        // Map Order utama
        return OrderResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .totalPrice(order.getTotalPrice())
                .shippingAddress(addressResponse)
                .items(itemResponses)
                .build();
    }
}  
