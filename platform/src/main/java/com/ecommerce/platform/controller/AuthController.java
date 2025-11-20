package com.ecommerce.platform.controller;

import com.ecommerce.platform.dto.AuthRequest;
import com.ecommerce.platform.dto.RegisterRequest;
import com.ecommerce.platform.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth") // Base path for all authentication endpoints
public class AuthController {

    private final AuthService authService;

    // Dependency Injection for the AuthService
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Handles POST requests to /api/auth/register.
     * Creates a new user account and returns a JWT upon success.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        // @RequestBody converts the incoming JSON (AuthRequest DTO) into a Java object
        String token = authService.register(request);
        return ResponseEntity.ok(token); // Returns 200 OK with the JWT in the body
    }

    /**
     * Handles POST requests to /api/auth/login.
     * Authenticates an existing user and returns a JWT upon success.
     */
    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody AuthRequest request) {
        String token = authService.authenticate(request);
        return ResponseEntity.ok(token); // Returns 200 OK with the JWT in the body
    }
}