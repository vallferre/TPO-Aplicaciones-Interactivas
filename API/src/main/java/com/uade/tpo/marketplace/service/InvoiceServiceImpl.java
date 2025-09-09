package com.uade.tpo.marketplace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.uade.tpo.marketplace.entity.Invoice;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.InvoiceResponse;
import com.uade.tpo.marketplace.exceptions.AccessDeniedException;
import com.uade.tpo.marketplace.repository.InvoiceRepository;

@Service
public class InvoiceServiceImpl implements InvoiceService{

    @Autowired
    private InvoiceRepository invoiceRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }

    // 🔹 Solo admin puede ver todas
    @Override
    public Page<InvoiceResponse> getAllInvoices(PageRequest pageRequest) throws AccessDeniedException {
        User currentUser = getCurrentUser();
        if (!User.RoleName.ADMIN.equals(currentUser.getRole())) {
            throw new AccessDeniedException();
        }

        return invoiceRepository.findAll(pageRequest)
                .map(InvoiceResponse::new);
    }

    // 🔹 Solo admin o dueño puede ver una factura
    @Override
    public InvoiceResponse getById(Long invoiceId) throws AccessDeniedException {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con id: " + invoiceId));

        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(invoice.getBuyer().getId())
            && !User.RoleName.ADMIN.equals(currentUser.getRole())) {
            throw new AccessDeniedException();
        }

        return new InvoiceResponse(invoice);
    }

    // 🔹 Solo buyer o admin
    @Override
    public Page<InvoiceResponse> getByBuyerId(Long buyerId, PageRequest pageRequest) throws AccessDeniedException {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(buyerId) && !User.RoleName.ADMIN.equals(currentUser.getRole())) {
            throw new AccessDeniedException();
        }

        return invoiceRepository.findByBuyerId(buyerId, pageRequest)
                .map(InvoiceResponse::new);
    }

    // 🔹 Solo seller o admin
    @Override
    public Page<InvoiceResponse> getBySellerId(Long sellerId, PageRequest pageRequest) throws AccessDeniedException {
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(sellerId) && !User.RoleName.ADMIN.equals(currentUser.getRole())) {
            throw new AccessDeniedException();
        }

        return invoiceRepository.findBySellerId(sellerId, pageRequest)
                .map(InvoiceResponse::new);
    }

}
