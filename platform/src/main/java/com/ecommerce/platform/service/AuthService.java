package com.ecommerce.platform.service;

import com.ecommerce.platform.dto.AuthRequest;
import com.ecommerce.platform.dto.RegisterRequest;
import com.ecommerce.platform.model.User;
import com.ecommerce.platform.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Spring injects all required beans through the constructor
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Creates a new user, hashes the password, saves to the DB, and generates a JWT.
     */
    public String register(RegisterRequest request) {
        // 1. Create User object and HASH THE PASSWORD
        var user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // IMPORTANT: Always hash passwords!
        
        // 2. Save the user
        userRepository.save(user);

        // 3. Generate a token (logs the user in immediately)
        return jwtService.generateToken(user);
    }

    /**
     * Authenticates a user using Spring Security and returns a JWT if successful.
     */
    public String authenticate(AuthRequest request) {
        // 1. Attempt to authenticate the user's credentials
        // If credentials are bad, the AuthenticationManager throws an exception.
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );
        
        // 2. If authentication succeeds, retrieve the fully loaded User entity
        var user = userRepository.findByUsername(request.getUsername())
                                 .orElseThrow(() -> new RuntimeException("User not found after successful authentication."));
                                 
        // 3. Generate the token and return it
        return jwtService.generateToken(user);
    }
}