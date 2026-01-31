package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.CartItemDTO;
import com.ecommerce.platform.model.CartItem;
import com.ecommerce.platform.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart") // Separate base path for cart actions
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // GET /api/cart - View your personal cart
    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getCart(Authentication authentication) {
    return ResponseEntity.ok(cartService.getCartItems(authentication.getName()));
    }

    // POST /api/cart/add/{productId}
    @PostMapping("/add/{productId}")
    public ResponseEntity<CartItemDTO> addToCart(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            Authentication authentication) {
        
        CartItemDTO item = cartService.addToCart(authentication.getName(), productId, quantity);
        return ResponseEntity.ok(item);
    }

    // DELETE /api/cart/{cartItemId}
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long cartItemId, Authentication authentication) {
        cartService.removeFromCart(cartItemId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}