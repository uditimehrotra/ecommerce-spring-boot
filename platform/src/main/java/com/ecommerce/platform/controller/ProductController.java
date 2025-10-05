package com.ecommerce.platform.controller;

import com.ecommerce.platform.model.Product;
import com.ecommerce.platform.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController // Marks this as a controller for RESTful services.
@RequestMapping("/api/products") // All endpoints in this class will start with "/api/products".
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Handles GET requests to /api/products to fetch all products.
     * @return A list of all products.
     */
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    /**
     * Handles GET requests to /api/products/{id} to fetch a single product.
     * @param id The ID of the product, extracted from the URL path.
     * @return The product if found (200 OK), or a 404 Not Found response.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        // Using ResponseEntity to return a proper 404 status if the product is not found.
        return product.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
}