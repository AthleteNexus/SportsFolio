package com.tech.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@sportsfolio.com}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends OTP to the specified email address
     * @param email Recipient email address
     * @param otp OTP code to send
     */
    public void sendOTPEmail(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("SportsFolio - Email Verification OTP");
            message.setText(buildOTPEmailBody(otp));

            mailSender.send(message);
            logger.info("OTP email sent successfully to: {}", email);
        } catch (Exception e) {
            logger.error("Failed to send OTP email to: {}", email, e);
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
        }
    }

    /**
     * Builds the OTP email body
     * @param otp OTP code
     * @return Email body as String
     */
    private String buildOTPEmailBody(String otp) {
        return "Dear User,\n\n" +
                "Thank you for signing up to SportsFolio!\n\n" +
                "Your One-Time Password (OTP) for email verification is:\n\n" +
                otp + "\n\n" +
                "This OTP is valid for 10 minutes. Please do not share this code with anyone.\n\n" +
                "If you didn't request this OTP, please ignore this email.\n\n" +
                "Best regards,\n" +
                "SportsFolio Team";
    }
}
