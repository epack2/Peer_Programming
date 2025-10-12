package exception;

/**
 * Thrown when enrollment-specific errors occur (duplicates, not found, etc.).
 */
public class EnrollmentException extends RuntimeException {
    public EnrollmentException(String message) { super(message); }
}
