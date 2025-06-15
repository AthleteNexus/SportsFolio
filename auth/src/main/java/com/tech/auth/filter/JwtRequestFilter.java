package com.tech.auth.filter;

import com.tech.auth.service.CustomUserDetailsService;
import com.tech.auth.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    UserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        System.out.println("JwtRequestFilter: Processing request for JWT authentication");
        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        System.out.println("Authorization Header: " + authorizationHeader);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }
        System.out.println("Extracted Username: " + username);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("Loading user details for username: " + username);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            System.out.println("User details loaded: " + userDetails);
            if (jwtUtil.validateToken(userDetails, username, jwt)) {
                System.out.println("JWT is valid, setting authentication in security context");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            System.out.println("JWT validation complete, proceeding with request");
        }
        System.out.println("Proceeding with filter chain");
        chain.doFilter(request, response);
        System.out.println("JwtRequestFilter: Request processing complete");
    }
}

