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
import com.uade.tpo.marketplace.entity.dto.ProductResponse;
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
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page == null || size == null) {
            return ResponseEntity.ok(
                    productService.getProducts(PageRequest.of(0, Integer.MAX_VALUE))
                                  .map(ProductResponse::from)
            );
        }
        return ResponseEntity.ok(
                productService.getProducts(PageRequest.of(page, size))
                              .map(ProductResponse::from)
        );
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long productId) {
        Optional<Product> result = productService.getProductById(productId);
        return result.map(p -> ResponseEntity.ok(ProductResponse.from(p)))
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
                             .body(ProductResponse.from(result));
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
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @RequestBody Product updatedProduct,
            @AuthenticationPrincipal User currentUser) throws ProductNotFoundException {

        Product result = productService.updateProduct(productId, updatedProduct, currentUser);
        return ResponseEntity.ok(ProductResponse.from(result));
    }

    // ==================== Endpoints extra ====================
    @GetMapping("/out-of-stock")
    public ResponseEntity<List<ProductResponse>> getOutOfStock() {
        return ResponseEntity.ok(
                productRepository.findOutOfStock().stream()
                        .map(ProductResponse::from)
                        .toList()
        );
    }

    @GetMapping("/cheaper-than")
    public ResponseEntity<List<ProductResponse>> getProductsCheaperThan(@RequestParam double price) {
        return ResponseEntity.ok(
                productRepository.findByPriceLessThan(price).stream()
                        .map(ProductResponse::from)
                        .toList()
        );
    }

    @GetMapping("/expensive-than")
    public ResponseEntity<List<ProductResponse>> getProductsMoreExpensiveThan(@RequestParam double price) {
        return ResponseEntity.ok(
                productRepository.findByPriceGreaterThan(price).stream()
                        .map(ProductResponse::from)
                        .toList()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String keyword) {
        return ResponseEntity.ok(
                productRepository.findByDescriptionContainingIgnoreCase(keyword).stream()
                        .map(ProductResponse::from)
                        .toList()
        );
    }

    @GetMapping("/order-by-price-asc")
    public ResponseEntity<List<ProductResponse>> getProductsOrderByPriceAsc() {
        return ResponseEntity.ok(
                productRepository.findAllByOrderByPriceAsc().stream()
                        .map(ProductResponse::from)
                        .toList()
        );
    }

    @GetMapping("/order-by-price-desc")
    public ResponseEntity<List<ProductResponse>> getProductsOrderByPriceDesc() {
        return ResponseEntity.ok(
                productRepository.findAllByOrderByPriceDesc().stream()
                        .map(ProductResponse::from)
                        .toList()
        );
    }

    @GetMapping("/order-by-stock")
    public ResponseEntity<List<ProductResponse>> getProductsOrderByStockDesc() {
        return ResponseEntity.ok(
                productRepository.findAllByOrderByStockDesc().stream()
                        .map(ProductResponse::from)
                        .toList()
        );
    }

    @GetMapping("/filter-by-username/{userId}")
    public ResponseEntity<List<ProductResponse>> getProductsBySpecificOwner(@PathVariable Long userId) {
        return ResponseEntity.ok(
                productRepository.findByOwner(userId).stream()
                        .map(ProductResponse::from)
                        .toList()
        );
    }

    @GetMapping("/by-category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productRepository.findByCategory(category).stream()
                        .map(ProductResponse::from)
                        .toList());
    }
}
