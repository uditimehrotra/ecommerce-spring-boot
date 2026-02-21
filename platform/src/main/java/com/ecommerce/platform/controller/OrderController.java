package com.ecommerce.platform.controller;

import com.ecommerce.platform.model.Order;
import com.ecommerce.platform.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(Authentication authentication) {
        // authentication.getName() gets the username from your JWT
        Order order = orderService.checkout(authentication.getName());
        return ResponseEntity.ok(order);
    }
}