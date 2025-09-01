package com.uade.tpo.marketplace.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

@ControllerAdvice
public class GlobalExceptionHandler {

    private void writeResponse(HttpServletResponse response, int status, String error, String message, String path) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");

        Map<String, Object> body = new HashMap<>();
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        body.put("path", path);

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }

    @ExceptionHandler(CategoryDuplicateException.class)
    public void handleCategoryDuplicate(CategoryDuplicateException ex, HttpServletRequest request, HttpServletResponse response) throws IOException {
        writeResponse(response, HttpStatus.BAD_REQUEST.value(), "Bad Request", "La categoria que se intenta agregar está duplicada", request.getRequestURI());
    }

    @ExceptionHandler(UserDuplicateException.class)
    public void handleUserDuplicate(CategoryDuplicateException ex, HttpServletRequest request, HttpServletResponse response) throws IOException{
        writeResponse(response, HttpStatus.BAD_REQUEST.value(), "Bad Request", "El usuario ya existe",request.getRequestURI());
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public void handleProdNotFound(CategoryDuplicateException ex, HttpServletRequest request, HttpServletResponse response) throws IOException{
        writeResponse(response, HttpStatus.BAD_REQUEST.value(), "Bad Request", "El producto no se encuentra",request.getRequestURI());
    }
}