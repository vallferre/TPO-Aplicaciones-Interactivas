package com.uade.tpo.marketplace.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.entity.dto.InvoiceResponse;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.service.InvoiceService;


@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    // 🔹 Solo admin puede ver todas
    @GetMapping
    public Page<InvoiceResponse> getAllInvoices(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) throws AccessDeniedException {
        return invoiceService.getAllInvoices(PageRequest.of(page, size)); 
    }

    // 🔹 Admin o dueño pueden ver una factura
    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable Long invoiceId) throws AccessDeniedException {
        return ResponseEntity.ok(invoiceService.getById(invoiceId));
    }

    // 🔹 Buyer o admin
    @GetMapping("/buyer/{buyerId}")
    public Page<InvoiceResponse> getByBuyer(@PathVariable Long buyerId,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) throws AccessDeniedException {
        return invoiceService.getByBuyerId(buyerId, PageRequest.of(page, size));
    }

    // 🔹 Seller o admin
    @GetMapping("/seller/{sellerId}")
    public Page<InvoiceResponse> getBySeller(@PathVariable Long sellerId,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "10") int size) throws AccessDeniedException {
        return invoiceService.getBySellerId(sellerId, PageRequest.of(page, size));
    }
}
