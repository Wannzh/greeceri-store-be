package com.greeceri.store.services;

import com.greeceri.store.models.entity.User;
import com.greeceri.store.models.request.CartRequest;
import com.greeceri.store.models.response.CartResponse;

public interface CartService {
    CartResponse getCart(User currentUser);
    CartResponse addItemToCart(User currentUser, CartRequest request);
    CartResponse removeItemFromCart(User currentUser, Long cartItemId);
    CartResponse clearCart(User currentUser);
}
