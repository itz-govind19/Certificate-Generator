package admin.myapp.com.authservice.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the login request payload containing user credentials.
 * This DTO is used to capture the username and password submitted
 * by the client during authentication.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * The username of the user attempting to log in.
     */
    private String username;

    /**
     * The password associated with the username.
     */
    private String password;
}
