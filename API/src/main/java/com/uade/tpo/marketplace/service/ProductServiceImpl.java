package com.uade.tpo.marketplace.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
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
        // Verifica duplicado considerando owner, name y description
        boolean exists = productRepository.existsByOwnerIdAndNameAndDescription(
                currentUser.getId(),
                product.getName(),
                product.getDescription()
        );
        if (exists) {
            throw new ProductDuplicateException();
        }

        // Asigna propietario
        product.setOwner(currentUser);

        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long productId, User currentUser) throws ProductNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        boolean isAdmin = currentUser.getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!product.getOwner().getId().equals(currentUser.getId()) && !isAdmin) {
            throw new RuntimeException("No tenés permiso para eliminar este producto");
        }

        productRepository.deleteById(productId);
    }

    public Product updateProduct(Long productId, Product productRequest, User currentUser) throws ProductNotFoundException {
    Product product = productRepository.findById(productId)
            .orElseThrow(ProductNotFoundException::new);

    if (!product.getOwner().getId().equals(currentUser.getId())) {
        throw new RuntimeException("No tenés permiso para modificar este producto");
    }

    // Actualizar campos si vienen en el body
    if (productRequest.getName() != null) {
        product.setName(productRequest.getName());
    }
    if (productRequest.getDescription() != null) {
        product.setDescription(productRequest.getDescription());
    }
    if (productRequest.getStock() > 0) {
        product.setStock(productRequest.getStock());
    }
    if (productRequest.getPrice() > 0) {
        product.setPrice(productRequest.getPrice());
    }

    //aplicar descuento si viene en el body
    if (productRequest.getDiscountPercentage() != null && productRequest.getDiscountPercentage() > 0) {
        double discount = productRequest.getDiscountPercentage();
        double newPrice = product.getPrice() - (product.getPrice() * discount / 100);
        product.setPrice(newPrice);
    }

    if (productRequest.getImages() != null) {
        product.setImages(productRequest.getImages());
    }
    if (productRequest.getVideos() != null) {
        product.setVideos(productRequest.getVideos());
    }
    if (productRequest.getCategories() != null && !productRequest.getCategories().isEmpty()) {
        product.setCategories(productRequest.getCategories());
    }

    return productRepository.save(product);
}
}

