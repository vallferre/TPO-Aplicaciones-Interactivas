package com.uade.tpo.marketplace.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.exceptions.ProductDuplicateException;
import com.uade.tpo.marketplace.exceptions.ProductNotFoundException;

public interface ProductService {
    Page<Product> getProducts(PageRequest pageable);

    Optional<Product> getProductById(Long productId);

    Product createProduct(Product product, User currentUser) throws ProductDuplicateException;

    void deleteProduct(Long productId, User currentUser) throws ProductNotFoundException;

    Product updateStock(Long productId, int newStock, User currentUser) throws ProductNotFoundException;

    Product applyDiscount(Long productId, double discountPercentage, User currentUser) throws ProductNotFoundException;

    Product updatePrice(Long productId, double newPrice, User currentUser) throws ProductNotFoundException;

    Product updateDescription(Long productId, String newDescription, User currentUser) throws ProductNotFoundException;
}
