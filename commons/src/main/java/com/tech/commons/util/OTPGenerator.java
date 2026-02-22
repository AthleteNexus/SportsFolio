package com.tech.commons.util;

import java.security.SecureRandom;

public class OTPGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final int OTP_LENGTH = 6;

    /**
     * Generates a random 6-digit OTP code
     * @return OTP code as String
     */
    public static String generateOTP() {
        int otp = random.nextInt((int) Math.pow(10, OTP_LENGTH));
        return String.format("%0" + OTP_LENGTH + "d", otp);
    }
}
