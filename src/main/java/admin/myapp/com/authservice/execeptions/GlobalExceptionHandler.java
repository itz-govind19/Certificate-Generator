package admin.myapp.com.authservice.execeptions;


import admin.myapp.com.authservice.auth.ErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

/**
 * Global exception handler to capture and format exception responses across all controllers.
 */
@RestControllerAdvice
@Hidden // Hides this controller advice from Swagger documentation
public class GlobalExceptionHandler {

    /**
     * Builds a standardized error response body.
     *
     * @param message The error message to be returned.
     * @param status  The HTTP status for the response.
     * @param request The current HTTP request.
     * @return A ResponseEntity containing the formatted error response.
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse();
        body.setMessage(message);
        body.setStatus(status.value());
        body.setTimestamp(Instant.now());
        body.setError(status.getReasonPhrase());
        body.setPath(request.getRequestURI());
        return new ResponseEntity<>(body, status);
    }

    /**
     * Handles DuplicateUserException and returns HTTP 409.
     */
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<?> handleDuplicateUser(DuplicateUserException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request); // 409
    }

    /**
     * Handles InvalidCredentialsException and returns HTTP 401.
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<?> handleInvalidCredentials(InvalidCredentialsException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, request); // 401
    }

    /**
     * Handles InvalidTokenException and returns HTTP 401.
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidToken(InvalidTokenException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, request); // 401
    }

    /**
     * Handles InvalidUserException and returns HTTP 400.
     */
    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<?> handleInvalidUser(InvalidUserException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request); // 400
    }

    /**
     * Handles NoUserFoundException and returns HTTP 404.
     */
    @ExceptionHandler(NoUserFoundException.class)
    public ResponseEntity<?> handleNoUserFound(NoUserFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request); // 404
    }

    /**
     * Handles AccessDeniedException and returns HTTP 403.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        return buildErrorResponse("Unauthorized: " + ex.getMessage(), HttpStatus.FORBIDDEN, request); // 403
    }

    /**
     * Handles BadCredentialsException and returns HTTP 403.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleAccessDeniedException(BadCredentialsException ex, HttpServletRequest request) {
        return buildErrorResponse("Unauthorized: " + ex.getMessage(), HttpStatus.FORBIDDEN, request); // 403
    }

    /**
     * Handles UsernameNotFoundException and returns HTTP 404.
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleAccessDeniedException(UsernameNotFoundException ex, HttpServletRequest request) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request); // 403
    }

    /**
     * Catch-all handler for any unhandled exceptions. Returns HTTP 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex, HttpServletRequest request) {
        ex.printStackTrace(); // You may replace with logger
        return buildErrorResponse("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR, request); // 500
    }
}
