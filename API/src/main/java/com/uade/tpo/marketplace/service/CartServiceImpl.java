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
    @Override
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
        Cart cart = cartRepository.findByUser(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setItems(new ArrayList<>());
            newCart.setTotal(0);
            return newCart;
        });

        // 🔑 Validar que todos los productos del carrito sean del mismo owner
        if (!cart.getItems().isEmpty()) {
            boolean sameOwner = cart.getItems().stream()
                .map(item -> item.getProduct().getOwner().getId())
                .allMatch(ownerId -> ownerId.equals(product.getOwner().getId()));

            if (!sameOwner) {
                throw new RuntimeException("El carrito solo puede contener productos de un único vendedor.");
        }
}

        // Buscar si ya existe el producto en el carrito
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }

        // Actualizar total
        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        cart.setTotal(total);

        return cartRepository.save(cart);
    }

    //elimino producto del carrito
    @Transactional
    @Override
    public Cart removeProductFromCart(String productName, Long userId) throws AccessDeniedException {
        Cart cart = cartRepository.findByUser(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // Verificación de permisos
        if (!cart.getUser().getId().equals(userId)) {
            throw new AccessDeniedException();
        }

        // Buscar el item correspondiente
        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getProduct().getName().equals(productName))
                .findFirst()
                .orElse(null);

        if (itemToRemove != null) {
            if (itemToRemove.getQuantity() > 1) {
                // Solo decrementa 1
                itemToRemove.setQuantity(itemToRemove.getQuantity() - 1);
            } else {
                // Si la cantidad es 1, remover completamente
                cart.getItems().remove(itemToRemove);
            }
        }

        // Recalcular total
        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getPriceAtAddTime())
                .sum();
        cart.setTotal(total);

        return cartRepository.save(cart);
    }


    // Método para mostrar los items del carrito
    @Override
    public List<CartItem> getCartItems(Long userId, User requester) throws AccessDeniedException{
        boolean isAdmin = requester.getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!requester.getId().equals(userId) && !isAdmin) {
            throw new AccessDeniedException();
        }

        return cartRepository.findByUser(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado para el usuario con id: " + userId))
                .getItems();
    }

    // Método para vaciar el carrito
    @Transactional
    @Override
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUser(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        cart.getItems().clear();
        cart.setTotal(0);

        cartRepository.save(cart);
    }


    //convierto carrito en orden y descuento del stock
    @Transactional
    @Override
    public Order checkout(Long userId) throws AccessDeniedException, InsufficientStockException {
        Cart cart = cartRepository.findByUser(userId)
            .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        // Validate stock availability
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getStock() < item.getQuantity()) {
                throw new InsufficientStockException(
                    "No hay stock suficiente para el producto: " + item.getProduct().getDescription()
                );
            }
        }

        // Apply Checkout process
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setItems(new ArrayList<>());

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity());

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getPriceAtAddTime());

            order.getItems().add(orderItem);
        }

        // Usar métodos auxiliares
        order.setTotalAmount(cart.calculateTotal());
        order.setCount(cart.getItems().stream()
                        .mapToLong(CartItem::getQuantity)
                        .sum());

        // Vaciar carrito
        cart.getItems().clear();

        return orderRepository.save(order);
    }

    @Override
    public double getCartTotal(Long userId) {
    Cart cart = cartRepository.findByUser(userId)
            .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
    return cart.calculateTotal();
}

}
