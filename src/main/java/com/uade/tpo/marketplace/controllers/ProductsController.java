package com.uade.tpo.marketplace.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.marketplace.controllers.config.JwtService;
import com.uade.tpo.marketplace.entity.Category;
import com.uade.tpo.marketplace.entity.Product;
import com.uade.tpo.marketplace.entity.User;
import com.uade.tpo.marketplace.entity.dto.ProductRequest;
import com.uade.tpo.marketplace.entity.dto.ProductResponse;
import com.uade.tpo.marketplace.exceptions.CategoryNotFoundException;
import com.uade.tpo.marketplace.exceptions.ProductDuplicateException;
import com.uade.tpo.marketplace.exceptions.ProductNotFoundException;
import com.uade.tpo.marketplace.exceptions.UserNotFoundException;
import com.uade.tpo.marketplace.repository.CategoryRepository;
import com.uade.tpo.marketplace.repository.ProductRepository;
import com.uade.tpo.marketplace.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

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

    @GetMapping("/id/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long productId) throws ProductNotFoundException {
        Product product = productService.getProductById(productId)
            .orElseThrow(ProductNotFoundException::new);
            return ResponseEntity.ok(ProductResponse.from(product));
    }

    @GetMapping("/name/{productName}")
    public ResponseEntity<ProductResponse> getProductByName(@PathVariable String productName) {
        Optional<Product> result = productService.getProductByName(productName);
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
            Category existing = categoryRepository.findByDescription(
                    name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());
            if (existing == null) {
                throw new IllegalArgumentException("Categoría no encontrada");
            } else {
                categories.add(existing);
            }
        }

        Product newProduct = new Product();
        newProduct.setName(productRequest.getName());
        newProduct.setDescription(productRequest.getDescription());
        newProduct.setStock(productRequest.getStock() != null ? productRequest.getStock() : 0);
        newProduct.setPrice(productRequest.getPrice() != null ? productRequest.getPrice() : 0.0);

        // porcentaje de descuento
        newProduct.setDiscountPercentage(productRequest.getEffectiveDiscountPercentage());

        newProduct.setCategories(categories);

        Product result = productService.createProduct(newProduct, currentUser);

        return ResponseEntity.created(URI.create("/products/" + result.getId()))
                             .body(ProductResponse.from(result));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Object> deleteProduct(
            @PathVariable Long productId,
            @RequestHeader("Authorization") String authHeader) throws ProductNotFoundException {
            
        // Validar header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Extraer token
        String token = authHeader.substring(7);

        // Extraer username del token
        String username = jwtService.extractUsername(token);
        
        //  Buscar el usuario en la base
        User requester = userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + username));


        productService.deleteProduct(productId, requester);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductRequest updatedProduct,
            @AuthenticationPrincipal User currentUser) throws ProductNotFoundException {

        Product result = productService.updateProduct(productId, updatedProduct, currentUser);
        return ResponseEntity.ok(ProductResponse.from(result));
    }

    @DeleteMapping("/delete-category/{productId}")
    public ResponseEntity<ProductResponse> deleteCategory(
            @PathVariable Long productId,
            @RequestBody ProductRequest updatedProduct,
            @AuthenticationPrincipal User currentUser) throws ProductNotFoundException {

        Product result = productService.deleteCategory(productId, updatedProduct, currentUser);
        return ResponseEntity.ok(ProductResponse.from(result));
    }

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
                productRepository.findByFinalPriceLessThan(price).stream()
                    .map(ProductResponse::from)
                    .toList()
    );
}

    @GetMapping("/expensive-than")
    public ResponseEntity<List<ProductResponse>> getProductsMoreExpensiveThan(@RequestParam double price) {
        return ResponseEntity.ok(
                productRepository.findByFinalPriceGreaterThan(price).stream()
                    .map(ProductResponse::from)
                    .toList()
    );
}

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String keyword) {
        // Service devuelve List<Product>
        List<Product> results = productService.searchProductsByName(keyword);

        // Convertimos a ProductResponse
        List<ProductResponse> responseList = results.stream()
                .map(ProductResponse::from)
                .toList();

        return ResponseEntity.ok(responseList);
    }



    @GetMapping("/order-by-price-asc")
    public ResponseEntity<List<ProductResponse>> getProductsOrderByPriceAsc() {
        return ResponseEntity.ok(
                productRepository.findAllByOrderByFinalPriceAsc().stream()
                    .map(ProductResponse::from)
                    .toList()
    );
}

    @GetMapping("/order-by-price-desc")
    public ResponseEntity<List<ProductResponse>> getProductsOrderByPriceDesc() {
        return ResponseEntity.ok(
                productRepository.findAllByOrderByFinalPriceDesc().stream()
                    .map(ProductResponse::from)
                    .toList()
    );
}

    @GetMapping("/order-by-stock")
    public ResponseEntity<List<ProductResponse>> getProductsOrderByStockDesc() {
        return ResponseEntity.ok(
                productRepository.findByStockGreaterThanOrderByStockDesc(0).stream()
                        .map(ProductResponse::from)
                        .toList()
        );
    }

    @GetMapping("/filter-by-username")
    public ResponseEntity<List<ProductResponse>> getProductsBySpecificOwner(
        @RequestHeader("Authorization") String authHeader) {
        // Extraer token del header "Bearer <token>"
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7); // quitar "Bearer "

        // Extraer username del token
        String username = jwtService.extractUsername(token);

        // Buscar el usuario en la base
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + username));

        Long userId = user.getId();

        // Obtener productos del usuario
        List<ProductResponse> products = productRepository.findByOwner(userId)
                .stream()
                .map(ProductResponse::from)
                .toList();

        return ResponseEntity.ok(products);
    }


    @GetMapping("/by-category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable Long category) {
        List<ProductResponse> products = productRepository.findByCategoryId(category)
            .stream()
            .map(ProductResponse::from)
            .toList();

        if (products.isEmpty()) {
            throw new CategoryNotFoundException(category);
        }

        return ResponseEntity.ok(products);
    }
}
