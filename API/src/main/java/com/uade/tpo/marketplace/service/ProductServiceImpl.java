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

        if (!product.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tenés permiso para eliminar este producto");
        }

        productRepository.deleteById(productId);
    }

    // ===================== NUEVO MÉTODO =====================
    @Override
    public Product updateProduct(Long productId, Product updatedProduct, User currentUser) throws ProductNotFoundException {
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        if (!product.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tenés permiso para modificar este producto");
        }

        // Actualizar solo los campos que vengan en el body
        if (updatedProduct.getName() != null) product.setName(updatedProduct.getName());
        if (updatedProduct.getDescription() != null) product.setDescription(updatedProduct.getDescription());
        if (updatedProduct.getStock() >= 0) product.setStock(updatedProduct.getStock());
        if (updatedProduct.getPrice() >= 0) product.setPrice(updatedProduct.getPrice());
        if (updatedProduct.getCategories() != null) product.setCategories(updatedProduct.getCategories());
        if (updatedProduct.getImages() != null) product.setImages(updatedProduct.getImages());
        if (updatedProduct.getVideos() != null) product.setVideos(updatedProduct.getVideos());

        return productRepository.save(product);
    }
}
