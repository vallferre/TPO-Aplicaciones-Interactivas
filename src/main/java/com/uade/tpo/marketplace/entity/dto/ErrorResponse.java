package com.uade.tpo.marketplace.entity.dto;

import java.util.List;

import lombok.Data;

@Data
public class ErrorResponse {
    private String error;
    private String message;
    private String path;
    private int status;
    private String timestamp;
    private String exception;   // clase de la excepción
    private List<String> stacktrace;

    public ErrorResponse(String error, String message, String path, int status, Throwable ex) {
        this.error = error;
        this.message = message;
        this.path = path;
        this.status = status;
        this.timestamp = java.time.LocalDateTime.now().toString();
        this.exception = ex.getClass().getName();
        if (ex.getStackTrace() != null) {
            this.stacktrace = 
                java.util.Arrays.stream(ex.getStackTrace())
                    .map(StackTraceElement::toString)
                    .limit(5) // limitar a 5 líneas para no romper Postman
                    .toList();
        }
    }
}