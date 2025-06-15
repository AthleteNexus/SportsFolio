package com.tech.controller;

import com.tech.auth.dto.AuthRequest;
import com.tech.auth.dto.AuthResponse;
import com.tech.auth.service.AuthService;
import com.tech.auth.util.JwtUtil;
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
        System.out.println("hello");
        try {
            logger.info("Authenticating user: {}", request.getUsername());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            logger.info("User authenticated successfully: {}", request.getUsername());
            System.out.println("User authenticated successfully: " + request.getUsername());
        } catch(Exception e) {
            logger.info("Authentication failed for user: {}", request.getUsername() + " - " + e.getMessage());
            return ResponseEntity.status(401).body("Invalid credentials "+ e.getMessage());
        }
        String token = jwtUtil.generateToken(request.getUsername());
//        String token = "toekn";
        System.out.println("Generated JWT token for user:  token: "+ request.getUsername()+ token);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}

