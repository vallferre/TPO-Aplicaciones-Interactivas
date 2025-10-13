package com.uade.tpo.marketplace.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User with id " + id + " not found");
    }

    public UserNotFoundException(String string) {
        //TODO Auto-generated constructor stub
    }
}

