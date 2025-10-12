package com.uade.tpo.marketplace.service;

import java.util.List;

import com.uade.tpo.marketplace.entity.Cart;
import com.uade.tpo.marketplace.entity.CartItem;
import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.exceptions.EmptyCartException;
import com.uade.tpo.marketplace.exceptions.InsufficientStockException;
import com.uade.tpo.marketplace.exceptions.ProductNotFoundException;

public interface CartService {
    public Cart addProductToCart(Long userId, long  productId, int quantity) throws AccessDeniedException;
    public Cart removeProductFromCart(long  productId, Long userId) throws AccessDeniedException, ProductNotFoundException;
    public List<CartItem> getCartItems(Long userId) throws AccessDeniedException;
    public void clearCart(Long userId) throws AccessDeniedException;
    public Order checkout(Long userId) throws AccessDeniedException, InsufficientStockException, EmptyCartException;
    public double getCartTotal(Long userId) throws AccessDeniedException;
    public Cart get(Long userId) throws AccessDeniedException;
}
