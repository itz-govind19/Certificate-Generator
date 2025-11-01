package admin.myapp.com.authservice.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Represents the structure of an error response returned by the API.
 * Contains details about the error such as timestamp, status code,
 * error phrase, message, and the request path where the error occurred.
 */
@Schema(name = "ErrorResponse", description = "Error response details")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    /**
     * The timestamp indicating when the error occurred.
     * Example format: "2025-07-06T01:28:30.422Z"
     */
    @Schema(description = "Timestamp of the error", example = "2025-07-06T01:28:30.422Z")
    private Instant timestamp;

    /**
     * The HTTP status code corresponding to the error.
     * For example, 403 for Forbidden.
     */
    @Schema(description = "HTTP status code", example = "403")
    private int status;

    /**
     * The HTTP error phrase associated with the status code.
     * For example, "Forbidden".
     */
    @Schema(description = "Error phrase", example = "Forbidden")
    private String error;

    /**
     * A detailed error message explaining the cause of the error.
     * For example, "Unauthorized: Access Denied".
     */
    @Schema(description = "Error message", example = "Unauthorized: Access Denied")
    private String message;

    /**
     * The URI path of the request that resulted in the error.
     * For example, "/api/v1/admin/get".
     */
    @Schema(description = "Request path", example = "/api/v1/admin/get")
    private String path;
}
