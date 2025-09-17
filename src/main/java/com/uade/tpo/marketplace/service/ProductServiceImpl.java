package com.uade.tpo.marketplace.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.ProductRequest;
import com.uade.tpo.marketplace.entity.dto.ProductResponse;
import com.uade.tpo.marketplace.exceptions.ProductDuplicateException;
import com.uade.tpo.marketplace.exceptions.ProductNotFoundException;
import com.uade.tpo.marketplace.repository.CategoryRepository;
import com.uade.tpo.marketplace.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Page<Product> getProducts(PageRequest pageable) {
        return productRepository.findByStockGreaterThan(0, pageable);
    }

    @Override
    public Optional<Product> getProductById(Long productId) {
        return productRepository.findById(productId);
    }

    @Override
    public Optional<Product> getProductByName(String productName) {
        return productRepository.findByName(productName);
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

        if (currentUser.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new RuntimeException("Un usuario administrador no puede crear productos.");
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

    @Override
    public Product updateProduct(Long productId, ProductRequest productRequest, User currentUser) throws ProductNotFoundException {
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
        if (productRequest.getStock() != null) {
            if (productRequest.getStock() >= 0) {
                product.setStock(productRequest.getStock() + product.getStock());
            } else {
                throw new IllegalArgumentException("El stock no puede ser negativo");
            }
        }

        if (productRequest.getPrice() != null) {
            if (productRequest.getPrice() >= 1) {
                product.setPrice(productRequest.getPrice());
            } else {
                throw new IllegalArgumentException("El precio debe ser mayor o igual a 1");
            }
        }
        
        //aplicar descuento si viene en el body
        if (productRequest.getDiscount() != null && productRequest.getDiscount() > 0 && productRequest.getDiscount() <= 100) {
            double discount = productRequest.getDiscount();
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
            List<String> duplicadas = new ArrayList<>();
            if (productRequest.getCategories() != null && !productRequest.getCategories().isEmpty()) {
                for (String categoriaNombre : productRequest.getCategories()) {
                    Category foundCategory = categoryRepository.findByDescription(categoriaNombre);

                    if (foundCategory != null) {
                        if (!product.getCategories().contains(foundCategory)) {
                            product.getCategories().add(foundCategory);
                            productRequest.getCategories().remove(foundCategory.getDescription());
                        } else {
                            duplicadas.add(categoriaNombre); // guardamos la duplicada
                        }
                    }  
                }
                if (productRequest.getCategories().isEmpty()){
                    throw new IllegalArgumentException("El producto " + product.getName() + " ya contiene la categoria " + duplicadas);
                }
            }
        }

        return productRepository.save(product);
    }

    @Override
    public Product deleteCategory(Long productId, ProductRequest productRequest, User currentUser) throws ProductNotFoundException{
        Product product = productRepository.findById(productId)
                .orElseThrow(ProductNotFoundException::new);

        if (!product.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tenés permiso para modificar este producto");
        }
        
        if (productRequest.getCategories() != null && !productRequest.getCategories().isEmpty()) {
            List<String> categoryNames = product.getCategories()
                                    .stream()
                                    .map(Category::getDescription)
                                    .map(String::toLowerCase)
                                    .toList();

            for (String categoria : productRequest.getCategories()) {
                if (categoryNames.contains(categoria)) {
                    product.getCategories().remove(categoryRepository.findByDescription(categoria));
                } else {
                    throw new IllegalArgumentException("El producto " + product.getName() + " no contiene la categoria " + categoryNames);
                }
            }
        }
        return productRepository.save(product);
    }

    @Override
    public List<ProductResponse> findByCategory(String category) {
        return productRepository.findByCategory(category)
                            .stream()
                            .map(ProductResponse::from)
                            .toList();
    }
}

