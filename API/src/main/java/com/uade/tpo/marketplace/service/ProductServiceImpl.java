package com.uade.tpo.marketplace.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.exceptions.ProductDuplicateException;
import com.uade.tpo.marketplace.exceptions.ProductNotFoundException;
import com.uade.tpo.marketplace.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<Product> getProducts(PageRequest pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Optional<Product> getProductById(Long productId) {
        return productRepository.findById(productId);
    }

    @Override
    public Product createProduct(Product product, User currentUser) throws ProductDuplicateException {
        Optional<Product> existing = productRepository.findByDescription(product.getDescription());
        if (existing.isPresent()) {
            throw new ProductDuplicateException();
        }
        product.setOwner(currentUser); // asigna propietario aquí
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long productId, User currentUser) throws ProductNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        if (!product.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tenés permiso para eliminar este producto");
        }

        productRepository.deleteById(productId);
    }

    @Override
    public Product updateStock(Long productId, int newStock, User currentUser) throws ProductNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        if (!product.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tenés permiso para modificar este producto");
        }

        product.setStock(newStock);
        return productRepository.save(product);
    }

    @Override
    public Product applyDiscount(Long productId, double discountPercentage, User currentUser) throws ProductNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        if (!product.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tenés permiso para modificar este producto");
        }

        double newPrice = product.getPrice() - (product.getPrice() * discountPercentage / 100);
        product.setPrice(newPrice);
        return productRepository.save(product);
    }
}
