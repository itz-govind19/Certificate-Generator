package admin.myapp.com.authservice.execeptions;

/**
 * Exception thrown when a user is considered invalid,
 * such as when required roles are missing or the user does not meet specific criteria.
 */
public class InvalidUserException extends RuntimeException {

    /**
     * Constructs a new InvalidUserException with the specified detail message.
     *
     * @param message the detail message explaining why the user is invalid
     */
    public InvalidUserException(String message) {
        super(message);
    }
}
