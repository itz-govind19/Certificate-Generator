package admin.myapp.com.authservice.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String message = "Authentication failed";
        String errorCode = "AUTHENTICATION_ERROR";

        // Check for JWT error attributes set by the filter
        String jwtError = (String) request.getAttribute("jwt_error");
        String jwtErrorMessage = (String) request.getAttribute("jwt_error_message");

        if (jwtError != null && jwtErrorMessage != null) {
            message = jwtErrorMessage;
            switch (jwtError) {
                case "expired":
                    errorCode = "TOKEN_EXPIRED";
                    break;
                case "signature":
                    errorCode = "INVALID_SIGNATURE";
                    break;
                case "malformed":
                    errorCode = "MALFORMED_TOKEN";
                    break;
                case "invalid":
                    errorCode = "INVALID_TOKEN";
                    break;
                case "processing":
                    errorCode = "PROCESSING_ERROR";
                    break;
            }
        } else {
            message = authException.getMessage() != null ? authException.getMessage() : "Authentication failed";
        }

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED); // 401
        body.put("error", "Unauthorized");
        body.put("message", message);
        body.put("errorCode", errorCode);
        body.put("path", request.getRequestURI());

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getOutputStream().println(objectMapper.writeValueAsString(body));
    }
}
