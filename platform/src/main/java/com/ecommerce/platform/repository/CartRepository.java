package com.ecommerce.platform.repository;

import com.ecommerce.platform.model.CartItem;
import com.ecommerce.platform.model.User;
import com.ecommerce.platform.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartItem, Long> {
    // Find all items belonging to a specific user
    List<CartItem> findByUser(User user);

    // Check if a user already has a specific product in their cart
    Optional<CartItem> findByUserAndProduct(User user, Product product);
}