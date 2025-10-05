package com.ecommerce.platform.service;

import com.ecommerce.platform.model.Product;
import com.ecommerce.platform.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Marks this class as a Spring service component.
public class ProductService {

    private final ProductRepository productRepository;

    // We use constructor injection to get an instance of our ProductRepository.
    @Autowired // @Autowired tells Spring to automatically provide the required bean.
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Retrieves all products from the database.
     * @return A list of all products.
     */
    public List<Product> getAllProducts() {
        // We call the findAll() method provided by JpaRepository.
        return productRepository.findAll();
    }

    /**
     * Retrieves a single product by its ID.
     * @param id The ID of the product.
     * @return The product if found, otherwise null.
     */
    public Optional<Product> getProductById(Long id) {
        // We call the findById() method, which returns an Optional.
        // .orElse(null) is a simple way to return the product or null if it's not present.
        return productRepository.findById(id);
    }
}