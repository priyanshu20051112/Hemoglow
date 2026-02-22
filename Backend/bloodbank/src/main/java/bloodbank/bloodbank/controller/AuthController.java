package bloodbank.bloodbank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bloodbank.bloodbank.config.JwtUtil;
import bloodbank.bloodbank.entity.User;
import bloodbank.bloodbank.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private bloodbank.bloodbank.service.OrganizationService organizationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
    // Check if username/email already exists
    if (userService.userEXists(request.getUsername())) {
        return ResponseEntity.badRequest().body("Username already taken");
    }
    if (userService.existsByEmail(request.getEmail())) {
        return ResponseEntity.badRequest().body("Email already in use");
    }

    // Create user entity
    User newUser = new User();
    // Normalize username to avoid case-sensitivity issues
    newUser.setUsername(request.getUsername() == null ? null : request.getUsername().trim().toLowerCase());
    newUser.setEmail(request.getEmail());
    // Encode password before saving
    newUser.setPassword(userService.getPasswordEncoder().encode(request.getPassword()));
    newUser.setUserRole(request.getUserRole());
    
    // Set additional user fields
    newUser.setFullName(request.getFullName());
    newUser.setPhone(request.getPhone());
    newUser.setAddress(request.getAddress());

    User savedUser = userService.saveUser(newUser);

    // Optionally generate JWT for new user
    String token = jwtUtil.generateToken(savedUser.getUsername(), savedUser.getUserRole());

    // If this registration is for an organization, also create an Organization row
    Long createdOrgId = null;
    String organizationId = null;
    if (savedUser.getUserRole() != null && "ORGANIZATION".equalsIgnoreCase(savedUser.getUserRole())) {
        // Generate unique organization ID
        organizationId = "ORG-" + String.format("%06d", savedUser.getId()) + "-" + 
                        java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // Build organization entity using available fields. Name is optional; fall back to username.
        bloodbank.bloodbank.entity.Organization org = new bloodbank.bloodbank.entity.Organization();
        org.setOrganizationId(organizationId);
        org.setUsername(savedUser.getUsername());
        org.setPassword(savedUser.getPassword()); // already encoded
        org.setEmail(savedUser.getEmail());
        org.setName(request.getName() != null && !request.getName().isBlank() ? request.getName() : savedUser.getUsername());
        org.setPhone(request.getPhone());
        org.setAddress(request.getAddress());
        // Link to the saved user so organization.user_id is set
        org.setUser(savedUser);
        // Persist
        bloodbank.bloodbank.entity.Organization savedOrg = organizationService.saveOrganization(org);
        if (savedOrg != null) createdOrgId = savedOrg.getId();
    }

    return ResponseEntity.ok(new RegisterResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getUserRole(), token, createdOrgId, organizationId));
}

    @Data
    static class RegisterRequest {
        private String username;
        private String email;
        private String password;
        private String userRole;
        private String name; 
        private String fullName;
        private String phone;
        private String address;
    }

    @Data
    @AllArgsConstructor
    static class RegisterResponse {
        private Long id;
        private String username;
        private String role;
        private String token;
        private Long organizationId; // may be null for non-org users
        private String organizationCustomId; // Custom organization ID for organizations
    }
}
