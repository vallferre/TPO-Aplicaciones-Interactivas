package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.exceptions.ProductDuplicateException;
import com.uade.tpo.marketplace.exceptions.ProductNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public interface ProductService {
    Page<Product> getProducts(PageRequest pageable);

    Optional<Product> getProductById(Long productId);

    Product createProduct(Product product) throws ProductDuplicateException;

    void deleteProduct(Long productId) throws ProductNotFoundException;

    Product updateStock(Long productId, int newStock) throws ProductNotFoundException;

    Product applyDiscount(Long productId, double discountPercentage) throws ProductNotFoundException;
}
