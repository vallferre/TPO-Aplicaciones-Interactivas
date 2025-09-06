package com.uade.tpo.marketplace.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Invoices")
public class Invoice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date; 
    private Double total;
    
    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id", unique = true)
    private Order order;
    
    @ManyToOne 
    @JoinColumn(name = "buyer_id", nullable = false) 
    private User buyer; 

    @ManyToOne 
    @JoinColumn(name = "seller_id", nullable = false) 
    private User seller;
}