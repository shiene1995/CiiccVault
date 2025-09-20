package com.ciiccvault.CiiccVault;

public class InputFilterUtils {

    // Filter for valid usernames (only alphanumeric, 4–20 chars)
    public static boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9]{4,20}$");
    }

    // Filter for safe comments (basic XSS prevention)
    public static String sanitizeComment(String comment) {
        return comment.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    public static boolean isSafeText(String input) {
        return input != null && input.matches("^[\\w .,-]+$");
    }

    public static boolean isNumeric(String input) {
        return input != null && input.matches("\\d+");
    }

    public static boolean isValidInputLength(String input, int min, int max) {
        return input != null && input.length() >= min && input.length() <= max;
    }

    // Login/Signup Input Filtering
    public static boolean isStrongPassword(String password) {
        // At least 8 characters, 1 number, 1 special char, 1 upper case
        return password != null && password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$");
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$");
    }

    public static boolean isValidPHMobileNumber(String phone) {
        return phone != null && phone.matches("^(\\+639|09|639)\\d{9}$");
    }

    public static String normalizePHMobile(String phone) {
        if (phone == null) return null;

        // Remove spaces, dashes, parentheses
        phone = phone.replaceAll("[^\\d+]", "");

        if (phone.matches("^09\\d{9}$")) {
            return "+63" + phone.substring(1); // Convert to international
        } else if (phone.matches("^639\\d{9}$")) {
            return "+63" + phone.substring(2);
        } else if (phone.matches("^\\+639\\d{9}$")) {
            return phone;
        }

        return null; // Invalid
    }

}
