package util;


public final class ValidationUtil {
    private ValidationUtil() {}


    public static void requireNonEmpty(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) throw new ValidationException(fieldName + " must not be empty");
    }


    public static void requireBannerId(String bannerId) {
// simple check: starts with B and 8 digits (adjustable)
        if (bannerId == null || !bannerId.trim().matches("B\\d{8}")) {
            throw new ValidationException("Invalid Banner ID: " + bannerId);
        }
    }


    public static void requireGradeInRange(int g) {
        if (g < 0 || g > 100) throw new ValidationException("Grade out of bounds: " + g);
    }
}


class ValidationException extends RuntimeException {
    ValidationException(String msg) { super(msg); }
}