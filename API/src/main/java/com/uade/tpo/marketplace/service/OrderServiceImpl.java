package com.uade.tpo.marketplace.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.uade.tpo.marketplace.entity.Invoice;
import com.uade.tpo.marketplace.entity.Order;
import com.uade.tpo.marketplace.entity.User;
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
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId); // ya lo trae JpaRepository
    }

    @Override
    public Page<Order> getOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public Page<Order> getOrdersByUser(Pageable pageable, Long userId) {
        return orderRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional
    public Invoice generateInvoiceForOrder(Long orderId) {
        // Buscar la orden
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada con id: " + orderId));

        // Verificar si ya existe una invoice para esta order
        Optional<Invoice> existingInvoice = invoiceRepository.findByOrder(order);
        if (existingInvoice.isPresent()) {
            throw new RuntimeException("La orden ya tiene una factura generada.");
        }

        // Obtener el vendedor a partir del primer item
        if (order.getItems().isEmpty()) {
            throw new RuntimeException("La orden no tiene items, no se puede generar la factura.");
        }
        User seller = order.getItems().get(0).getProduct().getOwner();

        // Crear la factura
        Invoice invoice = new Invoice();
        invoice.setDate(LocalDate.now());
        invoice.setTotal(order.getTotalAmount());
        invoice.setOrder(order);
        invoice.setBuyer(order.getUser());
        invoice.setSeller(seller);

        // Persistir y devolver
        return invoiceRepository.save(invoice);
    }

    
}
