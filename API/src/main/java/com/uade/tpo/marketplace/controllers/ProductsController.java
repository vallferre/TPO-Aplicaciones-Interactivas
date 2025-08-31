package com.uade.tpo.marketplace.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

        // Obtener la lista de nombres de categorías desde el request
        List<String> categoryNames = productRequest.getCategories();
        if (categoryNames == null || categoryNames.isEmpty()) {
            return ResponseEntity.badRequest().body("Se requiere al menos una categoría");
        }

        // Buscar las categorías por sus nombres
        List<Category> categories = categoryRepository.findByDescriptionIn(categoryNames);
        if (categories.isEmpty()) {
            return ResponseEntity.badRequest().body("Categorías no encontradas");
        }

        // Crear el producto
        Product newProduct = new Product();
        newProduct.setName(productRequest.getName());
        newProduct.setDescription(productRequest.getDescription());
        newProduct.setStock(productRequest.getStock());
        newProduct.setPrice(productRequest.getPrice());
        newProduct.setImages(productRequest.getImages() != null ? productRequest.getImages() : new ArrayList<>());
        newProduct.setVideos(productRequest.getVideos() != null ? productRequest.getVideos() : new ArrayList<>());
        newProduct.setCategories(categories); // asignar múltiples categorías

        // Asignar propietario y guardar
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


    @PutMapping("/{productId}/stock")
    public ResponseEntity<Product> updateStock(
            @PathVariable Long productId,
            @RequestParam int stock,
            @AuthenticationPrincipal User currentUser) throws ProductNotFoundException {

        Product updatedProduct = productService.updateStock(productId, stock, currentUser);
        return ResponseEntity.ok(updatedProduct);
    }

    @PutMapping("/{productId}/discount")
    public ResponseEntity<Product> applyDiscount(
            @PathVariable Long productId,
            @RequestParam double percentage,
            @AuthenticationPrincipal User currentUser) throws ProductNotFoundException {

        Product updatedProduct = productService.applyDiscount(productId, percentage, currentUser);
        return ResponseEntity.ok(updatedProduct);
    }

    @PutMapping("/{productId}/price")
    public ResponseEntity<Product> updatePrice(
        @PathVariable Long productId,
        @RequestBody Map<String, Double> body,
        @AuthenticationPrincipal User currentUser) throws ProductNotFoundException {

        double newPrice = body.get("price");
        Product updatedProduct = productService.updatePrice(productId, newPrice, currentUser);
        return ResponseEntity.ok(updatedProduct);
    }


    @PutMapping("/{productId}/description")
    public ResponseEntity<Product> updateDescription(
            @PathVariable Long productId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal User currentUser) throws ProductNotFoundException {

        String newDescription = body.get("description");
        Product updatedProduct = productService.updateDescription(productId, newDescription, currentUser);
        return ResponseEntity.ok(updatedProduct);
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
