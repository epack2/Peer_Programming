package util;

import exception.ValidationException;

import java.util.regex.Pattern;

/**
 * Centralized validation helper.
 */
public final class ValidationUtil {
    private ValidationUtil() { }

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern BANNER = Pattern.compile("^[A-Za-z0-9]+$");

    public static void requireNonEmpty(String v, String name) {
        if (v == null || v.trim().isEmpty()) throw new ValidationException(name + " must not be empty");
    }

    public static void requireRange(int value, int min, int max, String name) {
        if (value < min || value > max) throw new ValidationException(name + " must be between " + min + " and " + max);
    }

    public static void validateEmail(String email) {
        requireNonEmpty(email, "Email");
        if (!EMAIL.matcher(email).matches()) throw new ValidationException("Invalid email: " + email);
    }

    public static void validateBannerId(String id) {
        requireNonEmpty(id, "Banner ID");
        if (!BANNER.matcher(id).matches()) throw new ValidationException("Invalid Banner ID: " + id);
    }
}
