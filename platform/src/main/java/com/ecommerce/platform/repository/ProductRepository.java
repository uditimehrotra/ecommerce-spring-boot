package com.ecommerce.platform.repository;

import com.ecommerce.platform.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Marks this interface as a Spring repository component.
public interface ProductRepository extends JpaRepository<Product, Long> {
    // That's it!
    // Spring Data JPA automatically provides methods like:
    // - save()
    // - findById()
    // - findAll()
    // - deleteById()
    // ...and many more, just by extending JpaRepository.
}