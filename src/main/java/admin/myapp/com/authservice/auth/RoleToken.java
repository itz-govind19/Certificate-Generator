package admin.myapp.com.authservice.auth;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "role_tokens")
@Data
public class RoleToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;   // UUID or secure random string

    @Column(nullable = false)
    private String role;    // e.g., ADMIN, SUPERADMIN

    @Column(nullable = false)
    private LocalDateTime expiresAt; // token expiry time

    @Column(nullable = false)
    private boolean used;   // true once consumed

    // Getters and setters
}

