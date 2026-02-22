package com.tech.service;

import com.tech.commons.exception.*;
import com.tech.dto.AuthRequest;
import com.tech.commons.enums.UserRole;
import com.tech.commons.util.EmailValidator;
import com.tech.commons.util.OTPGenerator;
import com.tech.commons.util.PasswordValidator;
import com.tech.commons.util.UsernameValidator;
import com.tech.dao.OTPDAO;
import com.tech.dao.UsersDAO;
import com.tech.entities.OTP;
import com.tech.entities.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {
    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UsersDAO usersDAO;
    private final OTPDAO otpDAO;
    private final UsernameValidator usernameValidator;
    private final PasswordValidator passwordValidator;
    private final EmailValidator emailValidator;
    private final EmailService emailService;

    @Value("${otp.validity.minutes}")
    private long OTP_VALIDITY_MINUTES;

    public AuthService(
            UsersDAO usersDAO, OTPDAO otpDAO, UsernameValidator usernameValidator, PasswordValidator passwordValidator,
            EmailValidator emailValidator, EmailService emailService
    ) {
        this.usersDAO = usersDAO;
        this.otpDAO = otpDAO;
        this.usernameValidator = usernameValidator;
        this.passwordValidator = passwordValidator;
        this.emailValidator = emailValidator;
        this.emailService = emailService;
    }


    public void signup(AuthRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String email = request.getEmailId();
        logger.info("Signing up user: {}", username);
        // Check if user already exists
        if( usersDAO.checkIfUserExists(username) ) {
            throw new DuplicateResourceException("User with this username already exists");
        }
        // Check if email already exists
        if( usersDAO.checkIfEmailExists(email) ) {
            throw new DuplicateResourceException("User with this email already exists");
        }
        // Validate username
        usernameValidator.validate(username);
        // Validate password
        passwordValidator.validate(password);
        // Validate email
        emailValidator.validate(email);

        // Create a new user entity and save it (initially not verified)
        Users user = new Users();
        user.setName(username);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(password));
        user.setEmail(email);
        user.setUserRole(UserRole.USER.toString());
        user.setEmailVerified(false);
        usersDAO.saveUser(user);

        // Generate and send OTP
        generateAndSendOTP(email);
        logger.info("User signed up successfully and OTP sent to email: {}", email);
    }

    /**
     * Generates OTP and sends it to the user's email
     * @param email User's email address
     */
    private void generateAndSendOTP(String email) {
        // Generate OTP
        String otpCode = OTPGenerator.generateOTP();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES);

        // Save OTP to database
        OTP otp = new OTP();
        otp.setEmail(email);
        otp.setOtpCode(otpCode);
        otp.setExpiresAt(expiresAt);
        otp.setIsVerified(false);
        otpDAO.saveOTP(otp);

        // Send OTP via email
        try {
            emailService.sendOTPEmail(email, otpCode);
            logger.info("OTP generated and sent to email: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send OTP email to: {}", email, e);
            // Delete the OTP record if email sending failed
            otpDAO.deleteOTP(otp);
            throw new RuntimeException("Failed to send OTP email. Please try signup again.");
        }
    }

    /**
     * Verifies the OTP and marks the email as verified
     * @param email User's email address
     * @param otp OTP code provided by user
     */
    public void verifyOTP(String email, String otp) {
        logger.info("Verifying OTP for email: {}", email);

        // Check if user exists
        Optional<Users> userOptional = usersDAO.findByUserEmail(email);
        if (userOptional.isEmpty()) {
            throw new UnauthorizedException("User not found with this email");
        }

        // condition to check if email is already verified
        if (userOptional.get().getEmailVerified()) {
            throw new ConflictException(String.format("Email: %s is already verified",userOptional.get().getEmail()));
        }

        // Find the OTP record
        Optional<OTP> otpOptional = otpDAO.findByEmailAndOTPCode(email, otp);
        if (otpOptional.isEmpty()) {
            throw new UnauthorizedException("Invalid OTP code");
        }

        OTP otpRecord = otpOptional.get();

        // Check if OTP is expired
        if (otpRecord.isExpired()) {
            throw new UnauthorizedException("OTP has expired. Please request a new OTP");
        }

        // Check if OTP is already verified
        if (otpRecord.getIsVerified()) {
            throw new UnauthorizedException("OTP has already been verified");
        }

        // Mark OTP as verified
        otpRecord.setIsVerified(true);
        otpDAO.saveOTP(otpRecord);

        // Mark email as verified in user entity
        Users user = userOptional.get();
        user.setEmailVerified(true);
        usersDAO.saveUser(user);

        logger.info("Email verified successfully for user: {}", email);
    }

    /**
     * Resends OTP to the user's email
     * @param email User's email address
     */
    public void resendOTP(String email) {
        logger.info("Resending OTP for email: {}", email);

        // Check if user exists
        Optional<Users> userOptional = usersDAO.findByUserEmail(email);
        if (userOptional.isEmpty()) {
            throw new UnauthorizedException("User not found with this email");
        }

        // Delete any existing unverified OTP for this email
        Optional<OTP> existingOTP = otpDAO.findLatestUnverifiedOTPByEmail(email);
        existingOTP.ifPresent(otpDAO::deleteOTP);

        // Generate and send new OTP
        generateAndSendOTP(email);
    }

    /**
     * Sends OTP for password reset to user's email
     * @param email User's email address
     */
    public void forgotPassword(String email) {
        logger.info("Password reset OTP requested for email: {}", email);

        // Check if user exists
        Optional<Users> userOptional = usersDAO.findByUserEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with this email");
        }

        // Validate email
        emailValidator.validate(email);

        // Delete any existing unverified OTP for this email
        Optional<OTP> existingOTP = otpDAO.findLatestUnverifiedOTPByEmail(email);
        existingOTP.ifPresent(otpDAO::deleteOTP);

        // Generate and send OTP for password reset
        generateAndSendOTP(email);
        logger.info("Password reset OTP sent to email: {}", email);
    }

    /**
     * Resets user password using OTP verification
     * @param email User's email address
     * @param otp OTP code provided by user
     * @param newPassword New password
     * @param confirmPassword Password confirmation
     */
    public void resetPasswordWithOTP(String email, String otp, String newPassword, String confirmPassword) {
        logger.info("Attempting password reset with OTP for email: {}", email);

        // Validate passwords match
        if (!newPassword.equals(confirmPassword)) {
            throw new ConflictException("Passwords do not match");
        }

        // Validate new password strength
        passwordValidator.validate(newPassword);

        // Check if user exists
        Optional<Users> userOptional = usersDAO.findByUserEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with this email");
        }

        // Find the OTP record
        Optional<OTP> otpOptional = otpDAO.findByEmailAndOTPCode(email, otp);
        if (otpOptional.isEmpty()) {
            throw new ValidationException("Invalid OTP code");
        }

        OTP otpRecord = otpOptional.get();

        // Check if OTP is expired
        if (otpRecord.isExpired()) {
            throw new ValidationException("OTP has expired. Please request a new OTP");
        }

        // Check if OTP is already verified
        if (otpRecord.getIsVerified()) {
            throw new ConflictException("OTP has already been verified");
        }

        // Mark OTP as verified
        otpRecord.setIsVerified(true);
        otpDAO.saveOTP(otpRecord);

        // Update user's password
        Users user = userOptional.get();
        user.setPasswordHash(new BCryptPasswordEncoder().encode(newPassword));
        usersDAO.saveUser(user);

        logger.info("Password reset successfully with OTP for user: {}", email);
    }
}


