package admin.myapp.com.authservice.execeptions;

/**
 * Exception thrown when a user provides incorrect login credentials
 * during the authentication process.
 */
public class InvalidCredentialsException extends RuntimeException {

    /**
     * Constructs a new InvalidCredentialsException with the specified detail message.
     *
     * @param message the detail message explaining the cause of the authentication failure
     */
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
