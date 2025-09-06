package com.uade.tpo.marketplace.service;

import java.util.List;

import com.uade.tpo.marketplace.entity.Cart;
import com.uade.tpo.marketplace.entity.CartItem;
import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.exceptions.InsufficientStockException;

public interface CartService {
    public Cart addProductToCart(Long userId, String productName, int quantity, User requester) throws AccessDeniedException;
    public Cart removeProductFromCart(String productName, Long userId) throws AccessDeniedException;
    public List<CartItem> getCartItems(Long userId, User requester) throws AccessDeniedException;
    public void clearCart(Long userId) throws AccessDeniedException;
    public Order checkout(Long userId) throws AccessDeniedException, InsufficientStockException;
    public double getCartTotal(Long userId);
    public Cart get(Long userId);
}
