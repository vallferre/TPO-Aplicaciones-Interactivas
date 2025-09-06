package com.uade.tpo.marketplace.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.uade.tpo.marketplace.entity.Invoice;
import com.uade.tpo.marketplace.entity.Order;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    //buscar facturas por id de buyer
    @Query("SELECT i FROM Invoice i WHERE i.buyer.id = :buyerId")
    List<Invoice> findByBuyerId(Long buyerId);

    //buscar facturas por id de seller
    @Query("SELECT i FROM Invoice i WHERE i.seller.id = :sellerId")
    List<Invoice> findBySellerId(Long sellerId);

    //buscar factura por id de orden
    @Query("SELECT i FROM Invoice i WHERE i.order.id = :orderId")
    Invoice findByOrderId(Long orderId);

    //buscar factura por orden
    @Query("SELECT i FROM Invoice i WHERE i.order = :order")
    Optional<Invoice> findByOrder(Order order);
}
