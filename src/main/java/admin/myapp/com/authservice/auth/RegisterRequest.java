package admin.myapp.com.authservice.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Represents the registration request payload containing
 * user details required for creating a new account.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /**
     * The full name of the user.
     */
    private String name;

    /**
     * The desired username for the new account.
     */
    private String username;

    /**
     * The password for the new account.
     */
    private String password;

    /**
     * The user's email address.
     */
    private String email;

    /**
     * The user's mobile phone number.
     */
    private String mobile;

    /**
     * The set of roles assigned to the user.
     * Example values: ["ADMIN"], ["USER"], etc.
     */
    private Set<String> roles;
}
