package com.uade.tpo.marketplace.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.ProductRequest;
import com.uade.tpo.marketplace.exceptions.ProductDuplicateException;
import com.uade.tpo.marketplace.exceptions.ProductNotFoundException;
import com.uade.tpo.marketplace.repository.CategoryRepository;
import com.uade.tpo.marketplace.repository.ProductRepository;
import com.uade.tpo.marketplace.service.ProductService;

@RestController
@RequestMapping("products")
public class ProductsController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<Page<Product>> getProducts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page == null || size == null)
            return ResponseEntity.ok(productService.getProducts(PageRequest.of(0, Integer.MAX_VALUE)));
        return ResponseEntity.ok(productService.getProducts(PageRequest.of(page, size)));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {
        Optional<Product> result = productService.getProductById(productId);
        return result.map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createProduct(
            @RequestBody ProductRequest productRequest,
            @AuthenticationPrincipal User currentUser) throws ProductDuplicateException {

        List<String> categoryNames = productRequest.getCategories();
        if (categoryNames == null || categoryNames.isEmpty()) {
            return ResponseEntity.badRequest().body("Se requiere al menos una categoría");
        }

        List<Category> categories = new ArrayList<>();
        for (String name : categoryNames) {
            List<Category> existing = categoryRepository.findByDescription(
                    name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());
            if (existing.isEmpty()) {
                Category newCategory = new Category(name);
                categoryRepository.save(newCategory);
                categories.add(newCategory);
            } else {
                categories.add(existing.get(0));
            }
        }

        Product newProduct = new Product();
        newProduct.setName(productRequest.getName());
        newProduct.setDescription(productRequest.getDescription());
        newProduct.setStock(productRequest.getStock());
        newProduct.setPrice(productRequest.getPrice());
        newProduct.setImages(productRequest.getImages() != null ? productRequest.getImages() : new ArrayList<>());
        newProduct.setVideos(productRequest.getVideos() != null ? productRequest.getVideos() : new ArrayList<>());
        newProduct.setCategories(categories);

        Product result = productService.createProduct(newProduct, currentUser);

        return ResponseEntity.created(URI.create("/products/" + result.getId()))
                .body(result);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Object> deleteProduct(
            @PathVariable Long productId,
            @AuthenticationPrincipal User currentUser) throws ProductNotFoundException {

        productService.deleteProduct(productId, currentUser);
        return ResponseEntity.noContent().build();
    }

    // ==================== NUEVO ENDPOINT UNIFICADO ====================
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long productId,
            @RequestBody Product updatedProduct,
            @AuthenticationPrincipal User currentUser) throws ProductNotFoundException {

        Product result = productService.updateProduct(productId, updatedProduct, currentUser);
        return ResponseEntity.ok(result);
    }

    // ==================== Endpoints extra ====================
    @GetMapping("/out-of-stock")
    public ResponseEntity<List<Product>> getOutOfStock() {
        return ResponseEntity.ok(productRepository.findOutOfStock());
    }

    @GetMapping("/cheaper-than")
    public ResponseEntity<List<Product>> getProductsCheaperThan(@RequestParam double price) {
        return ResponseEntity.ok(productRepository.findByPriceLessThan(price));
    }

    @GetMapping("/expensive-than")
    public ResponseEntity<List<Product>> getProductsMoreExpensiveThan(@RequestParam double price) {
        return ResponseEntity.ok(productRepository.findByPriceGreaterThan(price));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        return ResponseEntity.ok(productRepository.findByDescriptionContainingIgnoreCase(keyword));
    }

    @GetMapping("/order-by-price-asc")
    public ResponseEntity<List<Product>> getProductsOrderByPriceAsc() {
        return ResponseEntity.ok(productRepository.findAllByOrderByPriceAsc());
    }

    @GetMapping("/order-by-price-desc")
    public ResponseEntity<List<Product>> getProductsOrderByPriceDesc() {
        return ResponseEntity.ok(productRepository.findAllByOrderByPriceDesc());
    }

    @GetMapping("/order-by-stock")
    public ResponseEntity<List<Product>> getProductsOrderByStockDesc() {
        return ResponseEntity.ok(productRepository.findAllByOrderByStockDesc());
    }
}
