package admin.myapp.com.authservice.execeptions;

/**
 * Exception thrown when a user is not found in the system.
 * This can occur during operations like login, update, or retrieval by ID or username.
 */
public class NoUserFoundException extends RuntimeException {

    /**
     * Constructs a new NoUserFoundException with the specified detail message.
     *
     * @param message the detail message indicating why the user was not found
     */
    public NoUserFoundException(String message) {
        super(message);
    }
}
