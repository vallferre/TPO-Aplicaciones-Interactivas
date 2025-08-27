package com.uade.tpo.marketplace.repository;

import org.springframework.stereotype.Repository;

import com.uade.tpo.marketplace.entity.Order;

@Repository
public class OrderRepository {

    public Order save(Order order) {
      //hacer logica de guardado, hice esto para que no de error 
        return order;
    }

}
