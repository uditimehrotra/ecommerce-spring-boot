package com.ecommerce.platform.service;

import com.ecommerce.platform.dto.CartItemDTO; // Import your new DTO
import com.ecommerce.platform.model.User;
import com.ecommerce.platform.model.CartItem;
import com.ecommerce.platform.model.Product;
import com.ecommerce.platform.repository.CartRepository;
import com.ecommerce.platform.repository.UserRepository;
import com.ecommerce.platform.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // 1. Change return type to CartItemDTO
    public CartItemDTO addToCart(String username, Long productId, int quantity) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem item = cartRepository.findByUserAndProduct(user, product)
                .map(existingItem -> {
                    existingItem.setQuantity(existingItem.getQuantity() + quantity);
                    return cartRepository.save(existingItem);
                })
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setUser(user);
                    newItem.setProduct(product);
                    newItem.setQuantity(quantity);
                    return cartRepository.save(newItem);
                });
        
        return convertToDTO(item); // Map to DTO before returning
    }

    // 2. Change return type to List<CartItemDTO>
    public List<CartItemDTO> getCartItems(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return cartRepository.findByUser(user)
                .stream()
                .map(this::convertToDTO) // Convert each item in the list
                .collect(Collectors.toList());
    }

    public void removeFromCart(Long cartItemId, String username) {
        CartItem item = cartRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        if (!item.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized to remove this item");
        }
        
        cartRepository.delete(item);
    }

    // 3. Helper method for mapping (The "Cleaner")
    private CartItemDTO convertToDTO(CartItem item) {
        CartItemDTO dto = new CartItemDTO();
        dto.setCartItemId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setUnitPrice(item.getProduct().getPrice());
        dto.setQuantity(item.getQuantity());
        // Calculate subtotal on the fly
        dto.setSubtotal(item.getProduct().getPrice() * item.getQuantity());
        return dto;
    }
}