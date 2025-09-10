package com.uade.tpo.marketplace.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Falta de stock para completar la orden")
public class InsufficientStockException extends Exception{
    public InsufficientStockException(String message) {
        super(message);
    }
    
}
