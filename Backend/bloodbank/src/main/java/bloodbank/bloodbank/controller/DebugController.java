    // ...existing code...
package bloodbank.bloodbank.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bloodbank.bloodbank.entity.Organization;
import bloodbank.bloodbank.entity.User;
import bloodbank.bloodbank.service.OrganizationService;
import bloodbank.bloodbank.service.UserService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/debug")
public class DebugController {
    // List all organization usernames from both tables
    @GetMapping("/orgUsernames")
    public ResponseEntity<?> orgUsernames() {
        var orgEntities = organizationService.getAllPublicOrgs().stream()
            .map(dto -> organizationService.findById(dto.id()))
            .filter(org -> org != null && org.getUsername() != null)
            .map(org -> org.getUsername())
            .toList();
        var userOrgs = userService.getAllUsers().stream()
            .filter(u -> "ORGANIZATION".equalsIgnoreCase(u.getUserRole()))
            .map(u -> u.getUsername())
            .toList();
        return ResponseEntity.ok(java.util.Map.of(
            "organizationTableUsernames", orgEntities,
            "usersTableOrganizationUsernames", userOrgs
        ));
    }

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private UserService userService;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    @Autowired
    private bloodbank.bloodbank.config.JwtUtil jwtUtil;

    @GetMapping("/seedOrg")
    public ResponseEntity<Organization> seedOrganization() {
        // Create a test org if not exists
        String username = "testorg";
        Organization existing = organizationService.findByUsername(username);
        if (existing != null) return ResponseEntity.ok(existing);

        Organization o = new Organization();
        o.setName("Test Organization");
        o.setUsername(username);
        o.setEmail("testorg@example.com");
        o.setPassword(passwordEncoder.encode("Test@123"));
        o.setOpenForDonations(true);
        Organization saved = organizationService.saveOrganization(o);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/orgs")
    public ResponseEntity<List<Organization>> listOrgs() {
        var dtos = organizationService.getAllPublicOrgs();
        List<Organization> all = dtos.stream()
                .map(d -> organizationService.findById(d.id()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(all);
    }

    @GetMapping("/users/orgs")
    public ResponseEntity<List<User>> listUsersWithOrgRole() {
        List<User> users = userService.getAllUsers();
        List<User> filtered = users.stream().filter(u -> "ORGANIZATION".equals(u.getUserRole())).collect(Collectors.toList());
        return ResponseEntity.ok(filtered);
    }

    @GetMapping("/syncOrgsFromUsers")
    public ResponseEntity<?> syncOrgsFromUsers() {
        List<User> users = userService.getAllUsers();
        List<User> orgUsers = users.stream().filter(u -> "ORGANIZATION".equalsIgnoreCase(u.getUserRole())).collect(Collectors.toList());
        int created = 0;
        for (User u : orgUsers) {
            Organization existing = organizationService.findByUsername(u.getUsername());
            if (existing == null) {
                Organization o = new Organization();
                o.setUsername(u.getUsername());
                o.setPassword(u.getPassword());
                o.setEmail(u.getEmail());
                o.setName(u.getUsername());
                organizationService.saveOrganization(o);
                created++;
            }
        }
        return ResponseEntity.ok(java.util.Map.of("synced", orgUsers.size(), "created", created));
    }

    @GetMapping("/loginAsTestOrg")
    public ResponseEntity<?> loginAsTestOrg() {
        Organization org = organizationService.findByUsername("testorg");
        if (org == null) return ResponseEntity.status(404).body("Test org not found");
        String jwt = jwtUtil.generateToken(org.getUsername(), "ORGANIZATION");
        return ResponseEntity.ok(java.util.Map.of("username", org.getUsername(), "token", jwt));
    }

    // Debug: check if a user's password looks encoded (BCrypt) without returning the hash
    @GetMapping("/userInsights")
    public ResponseEntity<?> userInsights(String username) {
        try {
            User u = userService.findByUsername(username);
            if (u == null) return ResponseEntity.status(404).body("User not found");
            String stored = u.getPassword();
            boolean looksEncoded = stored != null && (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$"));
            return ResponseEntity.ok(java.util.Map.of("username", u.getUsername(), "passwordLooksEncoded", looksEncoded, "storedLen", stored == null ? 0 : stored.length()));
        } catch (Exception e) {
            return ResponseEntity.status(404).body("User not found or error: " + e.getMessage());
        }
    }

    // Debug: list all users with a flag if their stored password looks encoded
    @GetMapping("/usersSummary")
    public ResponseEntity<?> usersSummary() {
        var users = userService.getAllUsers();
        var list = users.stream().map(u -> java.util.Map.of(
            "username", u.getUsername(),
            "passwordLooksEncoded", u.getPassword() != null && (u.getPassword().startsWith("$2a$") || u.getPassword().startsWith("$2b$") || u.getPassword().startsWith("$2y$")),
            "storedLen", u.getPassword() == null ? 0 : u.getPassword().length()
        )).toList();
        return ResponseEntity.ok(list);
    }

    // One-time migration: encode any plaintext passwords for all users.
    // POST /api/debug/migratePasswords
    @PostMapping("/migratePasswords")
    public ResponseEntity<?> migratePasswords() {
        var users = userService.getAllUsers();
        int total = users.size();
        int encodedCount = 0;
        for (var u : users) {
            String p = u.getPassword();
            boolean looksEncoded = p != null && (p.startsWith("$2a$") || p.startsWith("$2b$") || p.startsWith("$2y$"));
            if (p != null && !looksEncoded) {
                // encode and save
                u.setPassword(passwordEncoder.encode(p));
                userService.saveUser(u);
                encodedCount++;
            }
        }

        return ResponseEntity.ok(java.util.Map.of("totalUsers", total, "migrated", encodedCount));
    }
}
