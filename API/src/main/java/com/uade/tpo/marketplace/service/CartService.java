package com.uade.tpo.marketplace.service;

import java.util.ArrayList;
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
import com.uade.tpo.marketplace.exceptions.InsufficientStockException;
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
    public Cart addProductToCart(Long userId, String productName, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + userId));

        Product product = productRepository.findByName(productName)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productName));

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
                .filter(item -> item.getProduct().getName().equals(productName))
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
    public Cart removeProductFromCart(Long cartId, String productName, Long userId) throws AccessDeniedException{
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Cart not found"));

        // ✅ Verificación de ownership
        if (!cart.getUser().getId().equals(userId)) {
            throw new AccessDeniedException();
        }

        Product product = productRepository.findByName(productName)
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
    public Order checkout(Long cartId, Long userId) throws AccessDeniedException, InsufficientStockException {
        // Obtener carrito
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        // Verificar ownership
        if (!cart.getUser().getId().equals(userId)) {
            throw new AccessDeniedException();
        }

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        // Primero validar stock de todos los productos antes de modificar nada
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getStock() < item.getQuantity()) {
                throw new InsufficientStockException(
                    "No hay stock suficiente para el producto: " + item.getProduct().getDescription()
                );
            }
        }

        // Crear orden
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setItems(new ArrayList<>());
        order.setCount((long) cart.getItems().size());

        // Crear OrderItems y actualizar stock
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getPriceAtAddTime());

            order.getItems().add(orderItem);
        }

        // Vaciar carrito correctamente
        cart.getItems().clear();
        cartRepository.save(cart);

        // Guardar y retornar la orden
        return orderRepository.save(order);
    }

}
