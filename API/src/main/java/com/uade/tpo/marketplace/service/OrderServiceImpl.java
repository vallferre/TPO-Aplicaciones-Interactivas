package com.uade.tpo.marketplace.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.uade.tpo.marketplace.entity.Invoice;
import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.InvoiceResponse;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.repository.InvoiceRepository;
import com.uade.tpo.marketplace.repository.OrderRepository;

import jakarta.transaction.Transactional;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Override
    public Optional<Order> getOrderById(Long orderId) throws AccessDeniedException {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Orden no encontrada con id: " + orderId));

        User currentUser = getCurrentUser();
        // Si el usuario no es admin y no es el dueño de la orden
        if (!currentUser.getId().equals(order.getUser().getId()) && !currentUser.getRole().equals(User.RoleName.ADMIN)) {
            throw new AccessDeniedException();
        }

        return Optional.of(order); // ya lo trae JpaRepository
    }

    @Override
    public Page<Order> getOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public Page<Order> getOrdersByUser(Pageable pageable, Long userId) throws AccessDeniedException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal(); // asumimos que tu UserDetailsService devuelve tu entidad User

        // Validar permisos
        if (!currentUser.getId().equals(userId) && !currentUser.getRole().equals(User.RoleName.ADMIN)) {
            throw new AccessDeniedException();
        }

        // Retornar órdenes del usuario
        return orderRepository.findByUserId(userId, pageable);
    }

    @Transactional
    @Override
    public InvoiceResponse generateInvoiceForOrder(Long orderId) throws AccessDeniedException {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Orden no encontrada con id: " + orderId));

        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(order.getUser().getId())) {
            throw new AccessDeniedException();
        }

        Optional<Invoice> existingInvoice = invoiceRepository.findByOrder(order);
        if (existingInvoice.isPresent()) {
            throw new RuntimeException("La orden ya tiene una factura generada.");
        }

        if (order.getItems().isEmpty()) {
            throw new RuntimeException("La orden no tiene items, no se puede generar la factura.");
        }

        User seller = order.getItems().get(0).getProduct().getOwner();

        Invoice invoice = new Invoice();
        invoice.setDate(LocalDate.now());
        invoice.setTotal(order.getTotalAmount());
        invoice.setOrder(order);
        invoice.setBuyer(order.getUser());
        invoice.setSeller(seller);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        return new InvoiceResponse(savedInvoice); // <--- devuelves el DTO
    }


    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal(); 
    }
    
}