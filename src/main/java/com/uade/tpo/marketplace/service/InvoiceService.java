package com.uade.tpo.marketplace.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.tpo.marketplace.entity.dto.InvoiceResponse;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;

public interface InvoiceService {

    Page<InvoiceResponse> getAllInvoices(PageRequest pageRequest) throws AccessDeniedException;

    InvoiceResponse getById(Long invoiceId) throws AccessDeniedException;

    Page<InvoiceResponse> getByBuyerId(Long buyerId, PageRequest pageRequest) throws AccessDeniedException;

    Page<InvoiceResponse> getBySellerId(Long sellerId, PageRequest pageRequest) throws AccessDeniedException;



}
