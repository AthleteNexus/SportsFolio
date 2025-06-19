package com.tech.auth.filter;

import com.tech.auth.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    UserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        logger.info("JwtRequestFilter: Processing request for JWT authentication");
        final String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }
        logger.info("Extracted Username: {}", username);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("Loading user details for username: {}", username);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            if (jwtUtil.validateToken(userDetails, username, jwt)) {
                logger.info("JWT is valid, setting authentication in security context");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            logger.info("JWT validation complete, proceeding with request");
        }
        chain.doFilter(request, response);
    }
}

