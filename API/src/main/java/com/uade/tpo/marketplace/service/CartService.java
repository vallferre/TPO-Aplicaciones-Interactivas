package com.uade.tpo.marketplace.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uade.tpo.marketplace.entity.Cart;
import com.uade.tpo.marketplace.entity.CartItem;
import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.entity.OrderItem;
import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.repository.CartRepository;
import com.uade.tpo.marketplace.repository.OrderRepository;
import com.uade.tpo.marketplace.repository.ProductRepository;
import com.uade.tpo.marketplace.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class CartService {
    
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    //agrego al carrito
    @Transactional
    public Cart addProductToCart(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productId));

        // Validación: un usuario no puede comprar su propio producto
        if (product.getOwner().getId().equals(userId)) {
            throw new RuntimeException("No se puede agregar al carrito un producto propio.");
        }

        // Obtener o crear carrito
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return newCart;
        });

        // Agregar producto al carrito
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    //elimino producto del carrito
    @Transactional
    public Cart removeProductFromCart(Long cartId, Long productId, Long userId) throws AccessDeniedException{
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));

        // ✅ Verificación de ownership
        if (!cart.getUser().getId().equals(userId)) {
            throw new AccessDeniedException();
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        cart.removeProduct(product);
        return cartRepository.save(cart);
    }

    // Método para mostrar los items del carrito
    public List<CartItem> getCartItems(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + userId));

        return cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado para el usuario con id: " + userId))
                .getItems();
    }

    // Método para vaciar el carrito
    public void clearCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + userId));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado para el usuario con id: " + userId));

        cart.getItems().clear();
        cartRepository.save(cart);
    }

    //convierto carrito en orden y descuento del stock
    @Transactional
    public Order checkout(Long cartId, Long userId) throws AccessDeniedException{
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));

        // ✅ Verificación de ownership
        if (!cart.getUser().getId().equals(userId)) {
            throw new AccessDeniedException();
    }

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
