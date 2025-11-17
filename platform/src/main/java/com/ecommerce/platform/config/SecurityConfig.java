package com.ecommerce.platform.config;

import com.ecommerce.platform.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Marks this class as a source of bean definitions for the application context.
@EnableWebSecurity // This is the main switch that enables web security in our Spring Boot app.
public class SecurityConfig {

    /**
     * Defines how to find a user. We tell Spring Security to use our UserRepository
     * and its findByUsername method.
     */
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    /**
     * Defines the password hashing algorithm. We must store passwords hashed, never in plain text.
     * BCrypt is the industry-standard algorithm.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * This bean defines the main security rules for our application.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Disable CSRF (Cross-Site Request Forgery)
            // We can do this because we are using stateless JWTs, not browser cookies for sessions.
            .csrf(csrf -> csrf.disable())

            // 2. Define authorization rules
            .authorizeHttpRequests(auth -> auth
                // Allow all requests to our authentication endpoints (login/register)
                .requestMatchers("/api/auth/**").permitAll() 
                // Any other request must be authenticated (i.e., requires a valid token)
                .anyRequest().authenticated()
            )

            // 3. Configure session management to be STATELESS
            // This tells Spring Security not to create or use HTTP sessions.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    /**
     * The AuthenticationProvider is the component that uses the UserDetailsService
     * and PasswordEncoder to verify a user's credentials during login.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserRepository userRepository) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService(userRepository));
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * The AuthenticationManager is the core component that processes an authentication request.
     * We'll need this later in our login API.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}