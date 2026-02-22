package bloodbank.bloodbank.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bloodbank.bloodbank.config.JwtUtil;
import bloodbank.bloodbank.entity.User;
import bloodbank.bloodbank.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;

@CrossOrigin(origins = "http://localhost:5500")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // ... AuthenticationManager not used: manual auth implemented in login()

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    // LOGIN (existing logic, now returns JWT)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Manual authentication flow to avoid provider/lookup mismatches.
        try {
            // normalize username similar to service
            String normalized = request.getUsername() == null ? null : request.getUsername().trim().toLowerCase();
        var maybe = userService.getAllUsers().stream()
            .filter(u -> {
            if (u.getUsername() == null || normalized == null) return false;
            return normalized.equals(u.getUsername().trim().toLowerCase());
            })
            .findFirst();

            if (maybe.isEmpty()) {
                logger.info("Login: user '{}' not found", request.getUsername());
                return ResponseEntity.status(401).body("Invalid username or password");
            }

            User existing = maybe.get();
            
            // Check if this user has the correct role for user login
            if (existing.getUserRole() != null && 
                ("ORGANIZATION".equalsIgnoreCase(existing.getUserRole()) || 
                 "ORGANISATION".equalsIgnoreCase(existing.getUserRole()))) {
                logger.info("Login: organization user '{}' attempted to login via user interface", request.getUsername());
                return ResponseEntity.status(401).body("Organization accounts should use organization login");
            }
            
            String stored = existing.getPassword();
            boolean looksEncoded = stored != null && (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$"));

            boolean passwordOk = false;
            if (stored != null) {
                if (looksEncoded) {
                    passwordOk = passwordEncoder.matches(request.getPassword(), stored);
                } else {
                    // stored appears plaintext — accept exact match and upgrade
                    if (stored.equals(request.getPassword())) {
                        passwordOk = true;
                        existing.setPassword(passwordEncoder.encode(request.getPassword()));
                        userService.saveUser(existing);
                        logger.info("Login: upgraded plaintext password for user '{}'", existing.getUsername());
                    }
                }
            }

            if (!passwordOk) {
                logger.info("Login: password mismatch for user '{}' (storedEncoded={})", request.getUsername(), looksEncoded);
                return ResponseEntity.status(401).body("Invalid username or password");
            }

            String token = jwtUtil.generateToken(existing.getUsername(), existing.getUserRole());
            if (token == null || token.isBlank()) {
                logger.error("Login: token generation failed for user {}", existing.getUsername());
                return ResponseEntity.status(500).body("Failed to generate token");
            }
            logger.info("Login successful for {}: tokenLen={}", existing.getUsername(), token.length());
            return ResponseEntity.ok(new LoginResponse(existing.getId(), existing.getUsername(), existing.getUserRole(), token));
        } catch (Exception e) {
            logger.error("Login error for {}: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(500).body("Internal error");
        }
    }

    @Data
    static class LoginRequest {

        private String username;
        private String password;
    }

    @Data
    @AllArgsConstructor
    static class LoginResponse {
        private Long id;
        private String username;
        private String role;
        private String token;
    }

    // Other user endpoints
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

}
