package com.tech.controller;

import com.tech.dto.AuthRequest;
import com.tech.dto.AuthResponse;
import com.tech.dto.OTPResendRequest;
import com.tech.dto.OTPVerificationRequest;
import com.tech.dto.TokenRequest;
import com.tech.auth.util.JwtUtil;
import com.tech.commons.constants.Constants;
import com.tech.commons.exception.UnauthorizedException;
import com.tech.dto.UserDTO;
import com.tech.entities.Users;
import com.tech.service.AuthService;
import com.tech.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(
            AuthService authService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService
    ) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            logger.info("Authenticating user: {}", request.getUsername());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            logger.info("User authenticated successfully: {}", request.getUsername());
        } catch(Exception e) {
            logger.info("Authentication failed for user: {}", request.getUsername() + " - " + e.getMessage());
            throw new UnauthorizedException("Invalid username or password");
        }

        // Check if user's email is verified
        Users user = userService.findByUsername(request.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!user.getEmailVerified()) {
            logger.info("Login attempted with unverified email: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "message", "Email not verified",
                            "code", "EMAIL_NOT_VERIFIED",
                            "email", user.getEmail()
                    ));
        }

        String accessToken = jwtUtil.generateToken(request.getUsername(), Constants.ACCESS_TOKEN_VALIDITY_SECONDS);
        String refreshToken = jwtUtil.generateToken(request.getUsername(), Constants.REFRESH_TOKEN_VALIDITY_SECONDS);
        logger.info("Generated access token and refresh token for user: {}", request.getUsername());
        // Optionally, you can save the tokens in the database or cache for further use
        userService.updateRefreshToken(request.getUsername(), refreshToken);
        UserDTO userDto = userService.getCurrentUser(request.getUsername());
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, userDto));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody AuthRequest request) {
        logger.info("Signing up user: {}", request.getUsername());
        Optional<Users> existingUser = userService.findByEmailId(request.getEmailId());
        if (existingUser.isPresent() && !existingUser.get().getEmailVerified()) {
            logger.info("User exists but email not verified: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message", "Email not verified",
                            "code", "EMAIL_NOT_VERIFIED",
                            "email", request.getEmailId()
                    ));
        }
        authService.signup(request);
        logger.info("User signed up successfully: {} - OTP sent to email", request.getEmailId());
        return ResponseEntity.status(HttpStatus.CREATED).body("User signed up successfully. OTP sent to your email. Please verify your email to login.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestBody OTPVerificationRequest request) {
        logger.info("Verifying OTP for email: {}", request.getEmail());
        authService.verifyOTP(request.getEmail(), request.getOtp());
        logger.info("OTP verified successfully for email: {}", request.getEmail());
        return ResponseEntity.ok("Email verified successfully. You can now login.");
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOTP(@RequestBody OTPResendRequest request) {
        logger.info("Resending OTP to email: {}", request.getEmail());
        authService.resendOTP(request.getEmail());
        logger.info("OTP resent successfully to email: {}", request.getEmail());
        return ResponseEntity.ok("OTP resent to your email. Please verify within 10 minutes.");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        String username = jwtUtil.extractUsername(refreshToken);
        Users user = userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Optional: check if token matches saved token
        if (!refreshToken.equals(user.getRefreshToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        String newAccessToken = jwtUtil.generateToken(user.getName(), Constants.ACCESS_TOKEN_VALIDITY_SECONDS); // 15 mins
        return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken, new UserDTO(user)));
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam(name = "username") String username) {
        logger.info("Logging out user: {}", username);
        Users user = userService.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        user.setRefreshToken(null); // Clear the refresh token
        userService.updateRefreshToken(username, null);
        logger.info("User logged out successfully: {}", username);
        return ResponseEntity.ok("User logged out successfully");
    }
}

