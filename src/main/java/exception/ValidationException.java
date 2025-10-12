package exception;

/**
 * Thrown when user input or domain constraints are violated.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) { super(message); }
}
