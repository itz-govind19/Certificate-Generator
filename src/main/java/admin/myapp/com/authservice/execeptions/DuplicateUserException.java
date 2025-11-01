package admin.myapp.com.authservice.execeptions;

/**
 * Exception thrown when attempting to register or create a user
 * with a username that already exists in the system.
 */
public class DuplicateUserException extends RuntimeException {

    /**
     * Constructs a new DuplicateUserException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public DuplicateUserException(String message) {
        super(message);
    }
}
