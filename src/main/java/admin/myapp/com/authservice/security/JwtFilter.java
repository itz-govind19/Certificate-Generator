package admin.myapp.com.authservice.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT filter that intercepts incoming HTTP requests to validate and parse JWT tokens.
 * If a valid token is present in the Authorization header, the user's authentication
 * is set in the Spring Security context.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * Constructor-based dependency injection.
     * Uses @Lazy to prevent circular dependency issues.
     *
     * @param jwtUtil            utility for JWT operations
     * @param userDetailsService service to load user details
     */
    public JwtFilter(JwtUtil jwtUtil, @Lazy UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Filters each HTTP request to check for a valid JWT token in the Authorization header.
     * If valid, the user is authenticated and the SecurityContext is updated.
     *
     * @param request     incoming HTTP request
     * @param response    HTTP response
     * @param filterChain filter chain for passing request/response to the next filter
     * @throws ServletException in case of servlet processing error
     * @throws IOException      in case of I/O error
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String jwt = authHeader.substring(7); // Remove "Bearer " prefix

            try {
                String username = jwtUtil.extractUsername(jwt);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    if (jwtUtil.validateToken(jwt)) {
                        String roles = jwtUtil.extractRoles(jwt); // comma-separated

                        List<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim()))
                                .collect(Collectors.toList());

                        // Create authentication token
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(username, null, authorities);

                        // Set the authentication in the context
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("Authorities: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                        System.out.println("Username: " + SecurityContextHolder.getContext().getAuthentication().getName());

                    }
                }
            } catch (ExpiredJwtException e) {
                // Token is expired - set attribute for entry point
                request.setAttribute("jwt_error", "expired");
                request.setAttribute("jwt_error_message", "JWT token has expired");
                throw new BadCredentialsException("JWT token has expired", e);
            } catch (SignatureException e) {
                // Token signature is invalid
                request.setAttribute("jwt_error", "signature");
                request.setAttribute("jwt_error_message", "JWT token signature is invalid");
                throw new BadCredentialsException("JWT token signature is invalid", e);
            } catch (MalformedJwtException e) {
                // Token is malformed
                request.setAttribute("jwt_error", "malformed");
                request.setAttribute("jwt_error_message", "JWT token is malformed");
                throw new BadCredentialsException("JWT token is malformed", e);
            } catch (JwtException e) {
                // Any other JWT-related exception
                request.setAttribute("jwt_error", "invalid");
                request.setAttribute("jwt_error_message", "JWT token is invalid");
                throw new BadCredentialsException("JWT token is invalid", e);
            } catch (Exception e) {
                // Any other unexpected exception
                request.setAttribute("jwt_error", "processing");
                request.setAttribute("jwt_error_message", "JWT token processing failed");
                throw new BadCredentialsException("JWT token processing failed", e);
            }
        }

        // Continue with the next filter in the chain
        filterChain.doFilter(request, response);
    }
}
