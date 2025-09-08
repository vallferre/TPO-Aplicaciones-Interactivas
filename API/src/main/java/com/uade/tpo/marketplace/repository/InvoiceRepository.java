package com.uade.tpo.marketplace.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.uade.tpo.marketplace.entity.Invoice;
import com.uade.tpo.marketplace.entity.Order;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Buscar facturas por id de buyer con pageable
    @Query("SELECT i FROM Invoice i WHERE i.buyer.id = :buyerId")
    Page<Invoice> findByBuyerId(Long buyerId, Pageable pageable);

    // Buscar facturas por id de seller con pageable
    @Query("SELECT i FROM Invoice i WHERE i.seller.id = :sellerId")
    Page<Invoice> findBySellerId(Long sellerId, Pageable pageable);

    // Buscar factura por orden
    @Query("SELECT i FROM Invoice i WHERE i.order = :order")
    Optional<Invoice> findByOrder(Order order);

}