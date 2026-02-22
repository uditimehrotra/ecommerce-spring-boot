package com.ecommerce.platform.controller;

import com.ecommerce.platform.service.GenAIService;
import com.ecommerce.platform.service.CartService;
import com.ecommerce.platform.dto.CartResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final GenAIService genAIService;
    private final CartService cartService;

    public AIController(GenAIService genAIService, CartService cartService) {
        this.genAIService = genAIService;
        this.cartService = cartService;
    }

    @GetMapping("/recommend")
    public ResponseEntity<String> getRecommendation(Authentication authentication) {
        // 1. Get the current cart for the user
        CartResponseDTO cart = cartService.getCart(authentication.getName());
        
        // 2. Extract just the product names
        List<String> productNames = cart.getItems().stream()
                .map(item -> item.getProductName())
                .toList();

        if (productNames.isEmpty()) {
            return ResponseEntity.ok("Your cart is empty! Add something to get a recommendation.");
        }

        // 3. Get AI recommendation
        return ResponseEntity.ok(genAIService.getSmartRecommendation(productNames));
    }

    @GetMapping("/generate")
    public String generate(@RequestParam String productName) {
        return genAIService.generateDescription(productName);
    }
}