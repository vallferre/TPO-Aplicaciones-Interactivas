package com.uade.tpo.marketplace.service;

import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;

import com.uade.tpo.marketplace.entity.*;
import com.uade.tpo.marketplace.repository.*;

import jakarta.transaction.Transactional;

public class CartService {
    
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    //agrego al carrito
    @Transactional
    public Cart addProductToCart(Long cartId, Long productId, int quantity){
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();
        
        if (product.getStock() < quantity){
            throw new RuntimeException("No hay stock suficiente");
        }
        cart.addProduct(product, quantity);
        return cartRepository.save(cart);
    }

    //elimino producto del carrito
    @Transactional
    public Cart removeProductFromCart(Long cartId, Long productId){
        Cart cart = cartRepository.findById(cartId).orElseThrow();
        Product product = productRepository.findById(productId).orElseThrow();

        cart.removeProduct(product);
        return cartRepository.save(cart);
    }

    //convierto carrito en orden y descuento del stock
    @Transactional
    public Order checkout(Long cartId){
        Cart cart = cartRepository.findById(cartId).orElseThrow();

        Order order = new Order();
        order.setUser(cart.getUser());

        for(CartItem cartItem : cart.getItems()){
            Product product = cartItem.getProduct();

            //verifico stock
            if(product.getStock() < cartItem.getQuantity()){
                throw new RuntimeException("No hay stock suficiente para el producto: " + product.getDescription());
        }

        //descontar stock
        product.setStock(product.getStock() - cartItem.getQuantity());
        productRepository.save(product);

        //crear orderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setPriceAtPurchase(cartItem.getPriceAtAddTime());

        order.getItems().add(orderItem);
        }
        cart.getItems().clear(); //vaciar carrito
        cartRepository.save(cart); //guardar carrito vacio

        return orderRepository.save(order); //guardar y retornar orden
    }

}
