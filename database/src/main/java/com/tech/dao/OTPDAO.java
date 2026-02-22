package com.tech.dao;

import com.tech.entities.OTP;
import com.tech.repository.OTPRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OTPDAO {

    private final OTPRepository otpRepository;

    public OTPDAO(OTPRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    public void saveOTP(OTP otp) {
        otpRepository.save(otp);
    }

    public void deleteOTP(OTP otp) {
        otpRepository.delete(otp);
    }

    public Optional<OTP> findLatestUnverifiedOTPByEmail(String email) {
        return otpRepository.findLatestUnverifiedOTPByEmail(email);
    }

    public Optional<OTP> findByEmailAndOTPCode(String email, String otpCode) {
        return otpRepository.findByEmailAndOTPCode(email, otpCode);
    }
}
