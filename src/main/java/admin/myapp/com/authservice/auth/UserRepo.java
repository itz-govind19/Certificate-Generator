package admin.myapp.com.authservice.auth;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides methods to perform CRUD operations and custom queries on users.
 */
@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    /**
     * Checks if a user with the given username exists.
     *
     * @param username the username to check for existence
     * @return true if a user with the username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds all users who have a specific role.
     *
     * @param roleName the name of the role to filter users by
     * @return a list of users having the specified role
     */
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.roleName = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

}
