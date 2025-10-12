package com.uade.tpo.marketplace.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.ProductRequest;
import com.uade.tpo.marketplace.entity.dto.ProductResponse;
import com.uade.tpo.marketplace.exceptions.ProductDuplicateException;
import com.uade.tpo.marketplace.exceptions.ProductNotFoundException;

public interface ProductService {
    Page<Product> getProducts(PageRequest pageable);

    Optional<Product> getProductById(Long productId);

    Optional<Product> getProductByName(String productName);

    Product createProduct(Product product, User currentUser) throws ProductDuplicateException;

    void deleteProduct(Long productId, User currentUser) throws ProductNotFoundException;

    Product updateProduct(Long productId, ProductRequest productRequest, User currentUser) throws ProductNotFoundException;

    Product deleteCategory(Long productId, ProductRequest productRequest, User currentUser) throws ProductNotFoundException;

    public List<ProductResponse> findByCategory(Long category);
}
