package admin.myapp.com.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

/**
 * Utility class for generating and validating JWT tokens.
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}") // time in minutes
    private long expiration;

    /**
     * Generates a signing key using the configured secret.
     *
     * @return Key object used to sign and verify JWTs
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generates a JWT token for the given username and roles.
     *
     * @param username the username to embed in the token
     * @param roles    the roles to include as a claim
     * @return signed JWT token string
     */
    public String generateToken(String username, Long userId, String roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .claim("userId", userId)
                .claim("session","login")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (expiration*60*1000)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extracts the username (subject) from a JWT token.
     *
     * @param token the JWT token
     * @return the username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the userId (subject) from a JWT token.
     *
     * @param token the JWT token
     * @return the userId
     */
    public Long extractUserId(String token) {
        return extractClaimUserId(token);
    }


    /**
     * Extracts the UserId claim from a JWT token.
     *
     * @param token the JWT token
     * @return UserId string
     */
    public Long extractClaimUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    /**
     * Extracts the roles claim from a JWT token.
     *
     * @param token the JWT token
     * @return roles string
     */
    public String extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", String.class);
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token the JWT token
     * @return expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a custom claim from the JWT using the provided resolver function.
     *
     * @param token          the JWT token
     * @param claimsResolver function to resolve the claim
     * @param <T>            return type of the claim
     * @return the resolved claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT token using the signing key.
     *
     * @param token the JWT token
     * @return Claims object
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks whether the JWT token is expired.
     *
     * @param token the JWT token
     * @return true if token is expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validates a JWT token by checking its expiration status.
     *
     * @param token the JWT token
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
