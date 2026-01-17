package com.anonLove.util;
import java.security.SecureRandom;

public class OtpGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final int OTP_LENGTH = 6;

    public static String generate() {
        int otp = random.nextInt(900000) + 100000; // 100000 ~ 999999
        return String.valueOf(otp);
    }
}
