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
public class CartServiceImpl implements CartService {
    
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
    public Cart addProductToCart(Long userId, String productName, int quantity, User requester) throws AccessDeniedException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + userId));

        Product product = productRepository.findByName(productName)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con nombre: " + productName));

        if (product.getOwner().getId().equals(userId)) {
            throw new RuntimeException("No se puede agregar al carrito un producto propio.");
        }

        if (!requester.getId().equals(userId)) {
            throw new AccessDeniedException();
        }

        // Obtener o crear carrito
        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setItems(new ArrayList<>());
            newCart.setTotal(0);
            return newCart;
        });

        // Buscar si el producto ya está en el carrito
        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setPriceAtAddTime(product.getPrice()); // opcional, actualizar precio congelado
        } else {
            // Crear nuevo CartItem
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setPriceAtAddTime(product.getPrice());
            newItem.setAddedAt(java.time.LocalDateTime.now());

            cart.getItems().add(newItem);
        }

        // Actualizar total del carrito
        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getPriceAtAddTime())
                .sum();
        cart.setTotal(total);

        return cartRepository.save(cart);
    }

    //elimino producto del carrito
    @Transactional
    public Cart removeProductFromCart(Long cartId, String productName, Long userId) throws AccessDeniedException {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (!cart.getUser().getId().equals(userId)) {
            throw new AccessDeniedException();
        }

        cart.getItems().removeIf(item -> item.getProduct().getName().equals(productName));

        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getPriceAtAddTime())
                .sum();
        cart.setTotal(total);

        return cartRepository.save(cart);
    }

    // Método para mostrar los items del carrito
    public List<CartItem> getCartItems(Long userId, User requester) throws AccessDeniedException{
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + userId));

        if (!requester.getId().equals(userId) && !requester.getRole().equals("ROLE_ADMIN")) {
        throw new AccessDeniedException();
    }
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado para el usuario con id: " + userId))
                .getItems();
    }

    // Método para vaciar el carrito
    @Transactional
    public void clearCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        cart.getItems().clear();
        cart.setTotal(0);

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

    public double getCartTotal(Long userId) {
    Cart cart = cartRepository.findByUser(userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado")))
            .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
    return cart.calculateTotal();
}

}
