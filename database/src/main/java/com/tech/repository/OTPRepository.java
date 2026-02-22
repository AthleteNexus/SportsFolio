package com.tech.repository;

import com.tech.entities.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTP, Long> {

    @Query("SELECT o FROM OTP o WHERE o.email = ?1 AND o.isVerified = false ORDER BY o.createdAt DESC")
    Optional<OTP> findLatestUnverifiedOTPByEmail(String email);

    @Query("SELECT o FROM OTP o WHERE o.email = ?1 AND o.otpCode = ?2 AND o.isVerified = false")
    Optional<OTP> findByEmailAndOTPCode(String email, String otpCode);
}
