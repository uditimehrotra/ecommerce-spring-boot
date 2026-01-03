package com.ecommerce.platform.config;

import com.ecommerce.platform.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter { // Not a component; will be declared as a bean in SecurityConfig to avoid circular dependency

    //By extending OncePerRequestFilter, you guarantee that this logic runs exactly one time for every single HTTP request.

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Constructor Injection
    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Try to get the JWT from the "Authorization" header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 2. Check if the header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // If no token, continue the filter chain without updating the SecurityContext
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract the token (remove "Bearer " prefix)
        jwt = authHeader.substring(7);

        // 4. Extract the username from the token using our JwtService
        username = jwtService.extractUsername(jwt);

        // 5. If username is found and user is not already authenticated...
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Load user details from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 6. Validate the token
            if (jwtService.isTokenValid(jwt, userDetails)) {
                
                // Create an Authentication token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                
                // Add details about the request (IP address, session ID, etc.)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 7. Update the SecurityContextHolder (Log the user in!)
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 8. Continue the filter chain
        filterChain.doFilter(request, response);
    }
}