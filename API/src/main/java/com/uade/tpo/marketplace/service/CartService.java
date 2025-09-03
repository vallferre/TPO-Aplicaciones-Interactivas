package com.uade.tpo.marketplace.service;

import java.util.List;

import com.uade.tpo.marketplace.entity.Cart;
import com.uade.tpo.marketplace.entity.CartItem;
import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.exceptions.InsufficientStockException;

public interface CartService {
    public Cart addProductToCart(Long userId, String productName, int quantity);
    public Cart removeProductFromCart(Long cartId, String productName, Long userId) throws AccessDeniedException;
    public List<CartItem> getCartItems(Long userId);
    public void clearCart(Long userId) throws AccessDeniedException;
    public Order checkout(Long cartId, Long userId) throws AccessDeniedException, InsufficientStockException;
}
