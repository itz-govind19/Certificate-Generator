package admin.myapp.com.authservice.execeptions;

/**
 * Exception thrown when a provided JWT token is invalid or expired.
 */
public class InvalidTokenException extends RuntimeException {

    /**
     * Constructs a new InvalidTokenException with the specified detail message.
     *
     * @param message the detail message explaining why the token is invalid
     */
    public InvalidTokenException(String message) {
        super(message);
    }
}
