package com.ecommerce.platform.dto;

// This class represents the JSON data sent to /api/auth/login
public class AuthRequest {
    private String username;
    private String password;

    // --- Getters and Setters are essential for Spring to read the JSON ---

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}