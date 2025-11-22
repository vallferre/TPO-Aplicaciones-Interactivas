package com.uade.tpo.marketplace.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.uade.tpo.marketplace.entity.Cart;
import com.uade.tpo.marketplace.entity.CartItem;
import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.entity.OrderItem;
import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.exceptions.EmptyCartException;
import com.uade.tpo.marketplace.exceptions.InsufficientStockException;
import com.uade.tpo.marketplace.exceptions.ProductNotFoundException;
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

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }

    // Agregar producto al carrito
    @Transactional
    @Override
    public Cart addProductToCart(Long userId, Long productId, int quantity) throws AccessDeniedException {
        User currentUser = getCurrentUser();

        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("No tienes permisos para realizar esta accion.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + productId));

        if (product.getOwner().getId().equals(userId)) {
            throw new RuntimeException("No se puede agregar al carrito un producto propio.");
        }

        // Obtener o crear carrito
        Cart cart = cartRepository.findByUser(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            newCart.setItems(new ArrayList<>());
            newCart.setTotal(0);
            return newCart;
        });

        // Agregar o actualizar item
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            if (item.getQuantity() + quantity > product.getStock()) {
                throw new RuntimeException("No hay stock suficiente para el producto: " + product.getDescription());
            }
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem();
            newItem.setProduct(product);
            if (quantity > product.getStock()) {
                throw new RuntimeException("No hay stock suficiente para el producto: " + product.getDescription());
            }
            newItem.setQuantity(quantity);
            newItem.setCart(cart);
            cart.getItems().add(newItem);
        }

        // Actualizar total
        cart.setTotal(cart.getItems().stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum());

        return cartRepository.save(cart);
    }

    // Remover producto del carrito
    @Transactional
    @Override
    public Cart removeProductFromCart(Long productId, Long userId, int number) throws AccessDeniedException, ProductNotFoundException {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("No tienes permisos para realizar esta accion.");
        }

        Cart cart = cartRepository.findByUser(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        CartItem itemToRemove = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(ProductNotFoundException::new);

        if (itemToRemove != null) {
            int newQuantity = itemToRemove.getQuantity() - number;
            
            if (newQuantity > 0) {
                // Si después de restar todavía queda cantidad, actualizar
                itemToRemove.setQuantity(newQuantity);
            } else {
                // Si la cantidad resultante es 0 o menos, eliminar el item completamente
                cart.getItems().remove(itemToRemove);
            }
        }

        cart.setTotal(cart.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getPriceAtAddTime())
                .sum());

        return cartRepository.save(cart);
    }

    // Mostrar items del carrito
    @Override
    public List<CartItem> getCartItems(Long userId) throws AccessDeniedException {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId) && !isAdmin(currentUser)) {
            throw new AccessDeniedException("No tienes permisos para realizar esta accion.");
        }

        return cartRepository.findByUser(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"))
                .getItems();
    }

    // Vaciar carrito
    @Transactional
    @Override
    public void clearCart(Long userId) throws AccessDeniedException {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("No tienes permisos para realizar esta accion.");
        }

        Cart cart = cartRepository.findByUser(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        cart.getItems().clear();
        cart.setTotal(0);
        cartRepository.save(cart);
    }

        
    // Checkout
    @Transactional
    @Override
    public Order checkout(Long userId) throws AccessDeniedException, InsufficientStockException, EmptyCartException {
        
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("No tienes permisos para realizar esta accion.");
        }

        // Obtener carrito
        Cart cart = cartRepository.findByUser(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        // Validar que no esté vacío
        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException("El carrito está vacío, no se puede realizar el checkout");
        }

        // Validar stock
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getStock() < item.getQuantity()) {
                throw new InsufficientStockException(
                    "No hay stock suficiente para el producto: " + item.getProduct().getDescription()
                );
            }
        }

        // Crear la orden
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setItems(new ArrayList<>());
        order.setOrderDate(java.time.LocalDate.now());

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity()); // descontar stock

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setProductIdSnapshot(product.getId());
            orderItem.setProductNameSnapshot(product.getName());
            orderItem.setProductDescriptionSnapshot(product.getDescription());
            orderItem.setProductPriceSnapshot(product.getPrice());
            orderItem.setProductDiscountedPriceSnapshot(product.getDiscountPercentage());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setProductQuantitySnapshot(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getPriceAtAddTime());

            order.getItems().add(orderItem);
        }

        // Calcular totales
        order.setTotalAmount(cart.calculateTotal());
        order.setCount(cart.getItems().stream()
                        .mapToLong(CartItem::getQuantity)
                        .sum());

        // Vaciar carrito
        cart.getItems().clear();
        cartRepository.save(cart); // opcional, para persistir el cambio

        // Guardar y devolver orden
        return orderRepository.save(order);
    }

    private boolean isAdmin(User user) {
        return user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }

    @Override
    public double getCartTotal(Long userId) throws AccessDeniedException {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId) && !isAdmin(currentUser)) {
            throw new AccessDeniedException("No tienes permisos para realizar esta accion.");
        }

        Cart cart = cartRepository.findByUser(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));

        return cart.calculateTotal();
    }

    // Obtener carrito completo
    @Override
    public Cart get(Long userId) throws AccessDeniedException {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId) && !isAdmin(currentUser)) {
            throw new AccessDeniedException("No tienes permisos para realizar esta accion.");
        }

        return cartRepository.findByUser(userId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
    }
}
