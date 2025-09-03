package com.uade.tpo.marketplace.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.uade.tpo.marketplace.entity.Invoice;

public interface InvoiceRepository {
    //buscar facturas por id de buyer
    List<Invoice> findByBuyerId(Long buyerId);

    //buscar facturas por id de seller
    List<Invoice> findBySellerId(Long sellerId);

    //buscar factura por id de orden
    Invoice findByOrderId(Long orderId);

}
