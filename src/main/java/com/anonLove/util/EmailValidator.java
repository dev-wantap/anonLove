package com.anonLove.util;

import java.util.regex.Pattern;

public class EmailValidator {

    private static final Pattern AC_KR_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.ac\\.kr$");
    private static final Pattern EDU_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.edu$");

    public static boolean isUniversityEmail(String email) {
        return AC_KR_PATTERN.matcher(email).matches() ||
                EDU_PATTERN.matcher(email).matches();
    }

    public static String extractUniversity(String email) {
        // example@snu.ac.kr -> Seoul National University
        // example@university.edu -> University
        String domain = email.substring(email.indexOf('@') + 1);
        String universityPart = domain.split("\\.")[0];
        return capitalize(universityPart);
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
