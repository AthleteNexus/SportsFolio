package com.tech.controller;

import com.tech.auth.dto.AuthRequest;
import com.tech.auth.dto.AuthResponse;
import com.tech.auth.util.JwtUtil;
import com.tech.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private AuthService authService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            logger.info("Authenticating user: {}", request.getUsername());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            logger.info("User authenticated successfully: {}", request.getUsername());
        } catch(Exception e) {
            logger.info("Authentication failed for user: {}", request.getUsername() + " - " + e.getMessage());
            return ResponseEntity.status(401).body("Invalid credentials "+ e.getMessage());
        }
        String token = jwtUtil.generateToken(request.getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AuthRequest request) {
        logger.info("Signing up user: {}", request.getUsername());
        authService.signup(request);
        logger.info("User signed up successfully: {}", request.getUsername());
        return ResponseEntity.ok("User signed up successfully");
    }
}

