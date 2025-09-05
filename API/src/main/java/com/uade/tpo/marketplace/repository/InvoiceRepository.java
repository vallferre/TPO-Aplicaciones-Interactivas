package com.uade.tpo.marketplace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.uade.tpo.marketplace.entity.Invoice;

public interface InvoiceRepository {
    //buscar facturas por id de buyer
    @Query("SELECT i FROM Invoice i WHERE i.buyer.id = :buyerId")
    List<Invoice> findByBuyerId(Long buyerId);

    //buscar facturas por id de seller
    @Query("SELECT i FROM Invoice i WHERE i.seller.id = :sellerId")
    List<Invoice> findBySellerId(Long sellerId);

    //buscar factura por id de orden
    @Query("SELECT i FROM Invoice i WHERE i.order.id = :orderId")
    Invoice findByOrderId(Long orderId);
}
