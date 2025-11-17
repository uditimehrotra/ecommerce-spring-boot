package com.ecommerce.platform.model;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Entity
// We name the table "app_user" to avoid potential conflicts with the reserved SQL keyword "USER".
@Table(name = "app_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // In a real application, you'd have a separate Role entity.
    // For simplicity, we'll use a simple string.
    private String role = "USER";

    // --- UserDetails Methods (Required by Spring Security) ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // This method provides the user's roles to Spring Security.
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public boolean isAccountNonExpired() {
        // Indicates whether the user's account has expired.
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Indicates whether the user is locked or unlocked.
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Indicates whether the user's credentials (password) has expired.
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Indicates whether the user is enabled or disabled.
        return true;
    }


    // --- Standard Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}