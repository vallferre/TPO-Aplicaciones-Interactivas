package com.uade.tpo.marketplace.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Transición de estado inválida para la orden")
public class InvalidOrderTransitionException extends Exception {
    public InvalidOrderTransitionException(String message) {
        super(message);
    }
    
}
