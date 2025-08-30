package com.uade.tpo.marketplace.service;

import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.exceptions.ProductDuplicateException;
import com.uade.tpo.marketplace.exceptions.ProductNotFoundException;
import com.uade.tpo.marketplace.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Page<Product> getProducts(PageRequest pageable) {
        return productRepository.findAll(pageable);
    }

    public Optional<Product> getProductById(Long productId) {
        return productRepository.findById(productId);
    }

    public Product createProduct(Product product) throws ProductDuplicateException {
        Optional<Product> existing = productRepository.findByDescription(product.getDescription());
        if (existing.isPresent()) {
            throw new ProductDuplicateException();
        }
        return productRepository.save(product);
    }

    public void deleteProduct(Long productId) throws ProductNotFoundException {
        if (!productRepository.existsById(productId)) {
            throw new ProductNotFoundException();
        }
        productRepository.deleteById(productId);
    }

    public Product updateStock(Long productId, int newStock) throws ProductNotFoundException {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setStock(newStock);
            return productRepository.save(product);
        }
        throw new ProductNotFoundException();
    }

    public Product applyDiscount(Long productId, double discountPercentage) throws ProductNotFoundException {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            double newPrice = product.getPrice() - (product.getPrice() * discountPercentage / 100);
            product.setPrice(newPrice);
            return productRepository.save(product);
        }
        throw new ProductNotFoundException();
    }
}
