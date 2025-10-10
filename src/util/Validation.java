package util;

import service.ValidationException;

import java.util.regex.Pattern;

public final class Validation {
    private static final Pattern BANNER = Pattern.compile("^B\\d{3,}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public static void requireNotBlank(String s, String field) throws ValidationException {
        if (s == null || s.trim().isEmpty()) throw new ValidationException(field + " must be provided");
    }

    public static void validateBanner(String banner) throws ValidationException {
        if (!BANNER.matcher(banner).matches())
            throw new ValidationException("Banner ID invalid (expected like B001)");
    }

    public static void validateEmail(String email) throws ValidationException {
        if (!EMAIL.matcher(email).matches())
            throw new ValidationException("Invalid email format");
    }

    public static void validateCapacity(int cap) throws ValidationException {
        if (cap < 1 || cap > 500) throw new ValidationException("Capacity must be between 1 and 500");
    }
}
