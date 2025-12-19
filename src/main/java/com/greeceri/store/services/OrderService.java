package com.greeceri.store.services;

import java.util.List;

import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.request.CheckoutRequest;
import com.greeceri.store.models.response.OrderResponse;

public interface OrderService {
    OrderResponse createOrderFromCart(User currentUser, CheckoutRequest request);

    List<OrderResponse> getMyOrders(User currentUser);

    OrderResponse getMyOrderDetails(User currentUser, String orderId);

    /**
     * User confirms that order has been delivered/received
     * Only works for orders with SHIPPED status
     */
    OrderResponse confirmDelivery(User currentUser, String orderId);
}
