package com.uade.tpo.marketplace.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }

    // Security
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleSpringAccessDenied(org.springframework.security.access.AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleCustomAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage());
    }

    // Category
    @ExceptionHandler(CategoryDuplicateException.class)
    public ResponseEntity<Map<String, Object>> handleCategoryDuplicate(CategoryDuplicateException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Duplicate category", ex.getMessage());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCategoryNotFound(CategoryNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Category not found", ex.getMessage());
    }

    // Cart
    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<Map<String, Object>> handleEmptyCart(EmptyCartException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Empty cart", ex.getMessage());
    }

    // Product
    @ExceptionHandler(ProductDuplicateException.class)
    public ResponseEntity<Map<String, Object>> handleProductDuplicate(ProductDuplicateException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Duplicate product", ex.getMessage());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFound(ProductNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Product not found", ex.getMessage());
    }

    // Users
    @ExceptionHandler(UserDuplicateException.class)
    public ResponseEntity<Map<String, Object>> handleUserDuplicate(UserDuplicateException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Duplicate user", ex.getMessage());
    }

    //Orders
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotFound(OrderNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Order not found", ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "User not found", ex.getMessage());
    }


    @ExceptionHandler(InvalidOrderTransitionException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidOrderTransition(InvalidOrderTransitionException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid order transition", ex.getMessage());
    }

    // Stock
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientStock(InsufficientStockException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Insufficient stock", ex.getMessage());
    }

    @ExceptionHandler(InvalidStockException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidStock(InvalidStockException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid stock", ex.getMessage());
    }

    // Generic
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Runtime error", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
    }


}
