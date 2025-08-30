package com.uade.tpo.marketplace.controllers;

import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.dto.ProductRequest;
import com.uade.tpo.marketplace.exceptions.ProductDuplicateException;
import com.uade.tpo.marketplace.exceptions.ProductNotFoundException;
import com.uade.tpo.marketplace.service.ProductService;
import com.uade.tpo.marketplace.repository.CategoryRepository;
import com.uade.tpo.marketplace.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @PostMapping
    public ResponseEntity<Object> createProduct(@RequestBody ProductRequest productRequest)
            throws ProductDuplicateException {

        Optional<Category> categoryOpt = categoryRepository.findById(productRequest.getCategoryId());
        if (categoryOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Categoría no encontrada");
        }

        Product newProduct = new Product();
        newProduct.setDescription(productRequest.getDescription());
        newProduct.setStock(productRequest.getStock());
        newProduct.setPrice(productRequest.getPrice());
        newProduct.setCategory(categoryOpt.get());

        newProduct.setImages(productRequest.getImages() != null ? productRequest.getImages() : new ArrayList<>());
        newProduct.setVideos(productRequest.getVideos() != null ? productRequest.getVideos() : new ArrayList<>());

        Product result = productService.createProduct(newProduct);

        return ResponseEntity.created(URI.create("/products/" + result.getId()))
                .body(result);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Object> deleteProduct(@PathVariable Long productId) throws ProductNotFoundException {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{productId}/stock")
    public ResponseEntity<Product> updateStock(
            @PathVariable Long productId,
            @RequestParam int stock) throws ProductNotFoundException {
        return ResponseEntity.ok(productService.updateStock(productId, stock));
    }

    @PutMapping("/{productId}/discount")
    public ResponseEntity<Product> applyDiscount(
            @PathVariable Long productId,
            @RequestParam double percentage) throws ProductNotFoundException {
        return ResponseEntity.ok(productService.applyDiscount(productId, percentage));
    }

    // Endpoints extra
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
