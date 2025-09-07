package com.uade.tpo.marketplace.entity.dto;

import java.time.LocalDate;

import com.uade.tpo.marketplace.entity.Invoice;

import lombok.Data;

@Data
public class InvoiceResponse {
    private Long invoiceId;
    private LocalDate date;
    private Long orderId;
    private Long buyerId;
    private String buyerName;
    private String buyerEmail;
    private Long sellerId;
    private String sellerName;
    private String sellerEmail;
    private Double total;

    public InvoiceResponse(Invoice invoice) {
        this.invoiceId = invoice.getId();
        this.date = invoice.getDate();
        this.orderId = invoice.getOrder().getId(); // solo el ID
        this.buyerId = invoice.getBuyer().getId();
        this.buyerName = invoice.getBuyer().getName();
        this.buyerEmail = invoice.getBuyer().getEmail();
        this.sellerId = invoice.getSeller().getId();
        this.sellerName = invoice.getSeller().getName();
        this.sellerEmail = invoice.getSeller().getEmail();
        this.total = invoice.getTotal();
    }
}
