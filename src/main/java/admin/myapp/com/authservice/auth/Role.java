package admin.myapp.com.authservice.auth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(name = "role_name", nullable = false) // Match database column name
    private String roleName; // "ADMIN", "USER", "FARMER", "TRACTOR_OWNER"

    // Optional: Add bidirectional mapping if needed
    // @ManyToMany(mappedBy = "roles")
    // private Set<User> users = new HashSet<>();
}
