package com.uade.tpo.marketplace.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "El producto que se intenta agregar ya existe")
public class ProductDuplicateException extends Exception {
    public ProductDuplicateException() {
        super("El producto que se intenta agregar ya existe");
    }
}
