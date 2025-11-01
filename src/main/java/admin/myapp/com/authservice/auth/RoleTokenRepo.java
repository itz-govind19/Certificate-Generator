package admin.myapp.com.authservice.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleTokenRepo extends JpaRepository<RoleToken, Long> {
    Optional<RoleToken> findByToken(String token);
}

