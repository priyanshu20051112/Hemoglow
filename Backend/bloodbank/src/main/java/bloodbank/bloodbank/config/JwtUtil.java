package bloodbank.bloodbank.config;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private Key key;

    @Value("${jwt.secret:}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms:3600000}")
    private long expirationMs;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (jwtSecret != null && !jwtSecret.isBlank()) {
            try {
                byte[] bytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
                this.key = Keys.hmacShaKeyFor(bytes);
                log.info("JWT secret loaded from configuration (tokens will survive restarts)");
            } catch (IllegalArgumentException ex) {
                // key too short or invalid for HMAC-SHA algorithms
                log.error("Configured JWT secret is invalid or too short; falling back to generated key. Provide a sufficiently long secret (e.g. 32+ bytes).", ex);
                this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            }
        } else {
            // fallback: generate an ephemeral key (not suitable for production)
            this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            log.warn("No jwt.secret configured — using a generated ephemeral key. Tokens will be invalid after restart. Set 'jwt.secret' in application.properties or env to persist tokens.");
        }
    }

    // Generate JWT token with username + role
    public String generateToken(String username, String role) {
        if (this.key == null) {
            // ensure key exists
            this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            log.warn("JWT key was null; generated fallback key. Set 'jwt.secret' to persist tokens across restarts.");
        }

        String token = Jwts.builder()
                .setSubject(username)
                .claim("role", "ROLE_" + role) // Always add ROLE_ prefix
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();

        if (token == null || token.isBlank()) {
            log.error("Generated JWT token is null or empty for user {}", username);
        } else {
            log.info("Generated JWT token for {} (len={})", username, token.length());
        }

        return token;
    }

    // Extract username from token
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Extract role from token
    public String extractRole(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }

    // Validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
