package com.greeceri.store.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
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
import com.greeceri.store.models.response.DeliveryValidationResponse;
import com.greeceri.store.models.response.OrderItemResponse;
import com.greeceri.store.models.response.OrderResponse;
import com.greeceri.store.repositories.AddressRepository;
import com.greeceri.store.repositories.CartRepository;
import com.greeceri.store.repositories.OrderRepository;
import com.greeceri.store.repositories.ProductRepository;
import com.greeceri.store.services.AdminNotificationService;
import com.greeceri.store.services.OrderService;
import com.greeceri.store.services.ShippingService;
import com.xendit.exception.XenditException;
import com.xendit.model.Invoice;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final ShippingService shippingService;
    private final AdminNotificationService notificationService;

    // Payment Gateway
    @Value("${app.payment.redirect.success}")
    private String successRedirectUrl;

    @Value("${app.payment.redirect.failure}")
    private String failureRedirectUrl;

    private static final double SERVICE_FEE = 1000.0;
    private static final double MINIMUM_ORDER_AMOUNT = 10000.0;

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

        // === SHIPPING VALIDATIONS ===

        // Validasi tanggal pengiriman
        if (!shippingService.isValidDeliveryDate(request.getDeliveryDate())) {
            throw new RuntimeException("Tanggal pengiriman tidak valid. Pilih tanggal hari ini atau setelahnya.");
        }

        // Validasi alamat dalam jangkauan pengiriman
        DeliveryValidationResponse deliveryValidation = shippingService.validateDeliveryAddress(shippingAddress);
        if (!deliveryValidation.isDeliverable()) {
            throw new RuntimeException(deliveryValidation.getMessage());
        }

        Double shippingCost = deliveryValidation.getShippingCost();
        Double distanceKm = deliveryValidation.getDistanceKm();

        // === END SHIPPING VALIDATIONS ===

        List<Long> selectedIds = request.getSelectedCartItemIds();

        List<CartItem> itemsToCheckout = cart.getItems().stream()
                .filter(item -> selectedIds.contains(item.getId()))
                .toList();

        if (itemsToCheckout.isEmpty()) {
            throw new RuntimeException("Tidak ada item yang dipilih untuk checkout");
        }

        // Create objek order dengan delivery info
        Order newOrder = Order.builder()
                .user(currentUser)
                .shippingAddress(shippingAddress)
                .status(OrderStatus.PENDING_PAYMENT)
                .deliveryDate(request.getDeliveryDate())
                .deliverySlot(request.getDeliverySlot())
                .shippingCost(shippingCost)
                .distanceKm(distanceKm)
                .items(new ArrayList<>())
                .build();

        double itemsSubtotal = 0.0;

        // Move Item from cart to Order
        for (CartItem cartItem : itemsToCheckout) {
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
                    .priceAtPurchase(product.getPrice())
                    .quantity(cartItem.getQuantity())
                    .build();

            newOrder.getItems().add(orderItem);

            // Update Stok Produk
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            itemsSubtotal += (product.getPrice() * cartItem.getQuantity());
        }

        // Minimum order validation
        if (itemsSubtotal < MINIMUM_ORDER_AMOUNT) {
            throw new RuntimeException(String.format(
                    "Minimum pembelian adalah Rp %.0f. Total belanja Anda saat ini Rp %.0f",
                    MINIMUM_ORDER_AMOUNT, itemsSubtotal));
        }

        // Calculate grand total: subtotal + shipping + service fee
        double grandTotal = itemsSubtotal + shippingCost + SERVICE_FEE;
        newOrder.setTotalPrice(grandTotal);

        // Simpan Order
        Order savedOrder = orderRepository.save(newOrder);

        // Get Invoice Payment Gateway
        String invoiceUrl;
        String xenditInvoiceId;

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("external_id", savedOrder.getId());
            params.put("amount", savedOrder.getTotalPrice());
            params.put("payer_email", currentUser.getEmail());
            params.put("description", "Payment for Greeceri Orders #" + savedOrder.getId());
            params.put("success_redirect_url", successRedirectUrl + "?orderId=" + savedOrder.getId());
            params.put("failure_redirect_url", failureRedirectUrl + "?orderId=" + savedOrder.getId());

            Invoice invoice = Invoice.create(params);
            invoiceUrl = invoice.getInvoiceUrl();
            xenditInvoiceId = invoice.getId();
        } catch (XenditException e) {
            throw new RuntimeException("Failed to create payment invoice:" + e.getMessage());
        }

        // Update Order dengan Id Xendit
        savedOrder.setXenditInvoiceId(xenditInvoiceId);
        orderRepository.save(savedOrder);

        // Kosongkan Keranjang
        cart.getItems().removeAll(itemsToCheckout);
        cartRepository.save(cart);

        // === SEND NOTIFICATION TO ADMIN ===
        notificationService.notifyNewOrder(savedOrder.getId(), currentUser.getName(), grandTotal);

        // Return DTO + PaymentUrl
        OrderResponse response = mapOrderToResponse(savedOrder, itemsSubtotal);
        response.setPaymentUrl(invoiceUrl);

        return response;
    }

    @Override
    public List<OrderResponse> getMyOrders(User currentUser) {
        return orderRepository.findByUserOrderByOrderDateDesc(currentUser)
                .stream()
                .map(order -> mapOrderToResponse(order, null))
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getMyOrderDetails(User currentUser, String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pesanan tidak ditemukan"));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Akses ditolak. Pesanan ini bukan milik Anda.");
        }

        return mapOrderToResponse(order, null);
    }

    @Override
    @Transactional
    public OrderResponse confirmDelivery(User currentUser, String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pesanan tidak ditemukan"));

        // Validate ownership
        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Akses ditolak. Pesanan ini bukan milik Anda.");
        }

        // Only SHIPPED orders can be confirmed as delivered
        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new RuntimeException("Hanya pesanan dengan status SHIPPED yang dapat dikonfirmasi sebagai diterima.");
        }

        // Update status to DELIVERED
        order.setStatus(OrderStatus.DELIVERED);
        Order savedOrder = orderRepository.save(order);

        // === SEND NOTIFICATION TO ADMIN ===
        notificationService.notifyOrderDelivered(savedOrder.getId());

        return mapOrderToResponse(savedOrder, null);
    }

    private OrderResponse mapOrderToResponse(Order order, Double providedSubtotal) {
        // Map OrderItems
        List<OrderItemResponse> itemResponses = order.getItems().stream().map(item -> {
            String imageUrl = null;
            try {
                Product product = productRepository.findById(item.getProductId()).orElse(null);
                if (product != null) {
                    imageUrl = product.getImageUrl();
                }
            } catch (Exception e) {
            }

            return OrderItemResponse.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .productImageUrl(imageUrl)
                    .priceAtPurchase(item.getPriceAtPurchase())
                    .quantity(item.getQuantity())
                    .subTotal(item.getPriceAtPurchase() * item.getQuantity())
                    .build();
        }).collect(Collectors.toList());

        // Calculate subtotal from items if not provided
        Double subtotal = providedSubtotal;
        if (subtotal == null) {
            subtotal = order.getItems().stream()
                    .mapToDouble(item -> item.getPriceAtPurchase() * item.getQuantity())
                    .sum();
        }

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
                .latitude(order.getShippingAddress().getLatitude())
                .longitude(order.getShippingAddress().getLongitude())
                .build();

        // Map Order with shipping fields
        return OrderResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .subtotal(subtotal)
                .shippingCost(order.getShippingCost())
                .serviceFee(SERVICE_FEE)
                .totalPrice(order.getTotalPrice())
                .distanceKm(order.getDistanceKm())
                .deliveryDate(order.getDeliveryDate())
                .deliverySlot(order.getDeliverySlot())
                .shippingAddress(addressResponse)
                .items(itemResponses)
                .build();
    }
}
