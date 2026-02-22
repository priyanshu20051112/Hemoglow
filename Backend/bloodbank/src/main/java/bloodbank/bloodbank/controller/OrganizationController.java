package bloodbank.bloodbank.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bloodbank.bloodbank.service.OrganizationService;

@RestController
@RequestMapping("/api/org")
public class OrganizationController {
    // Endpoint to return all organizations for dropdowns
    @GetMapping("/organizations")
    public ResponseEntity<?> getAllOrganizations() {
        var orgs = organizationService.findAll();
        return ResponseEntity.ok(orgs);
    }

    // List all requests for an organization
    @Autowired
    private bloodbank.bloodbank.repository.RequestRepository requestRepository;

    @GetMapping("/{orgId}/requests")
    public ResponseEntity<?> listRequests(@PathVariable Long orgId) {
        var requests = requestRepository.findByOrganizationId(orgId);
        return ResponseEntity.ok(requests);
    }

    // Accept a blood request
    @PostMapping("/{orgId}/requests/{requestId}/accept")
    public ResponseEntity<?> acceptRequest(@PathVariable Long orgId, @PathVariable Long requestId) {
        var request = requestRepository.findById(requestId).orElse(null);
        if (request == null) return ResponseEntity.notFound().build();
        boolean handled = organizationService.handleBloodRequest(orgId, request, true);
        if (handled) {
            if ("REJECTED".equals(request.getStatus())) {
                return ResponseEntity.status(400).body("Not enough blood available. Request rejected.");
            }
            return ResponseEntity.ok(request);
        }
        return ResponseEntity.status(500).body("Failed to accept request");
    }

    // Reject a blood request
    @PostMapping("/{orgId}/requests/{requestId}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long orgId, @PathVariable Long requestId) {
        var request = requestRepository.findById(requestId).orElse(null);
        if (request == null) return ResponseEntity.notFound().build();
        boolean handled = organizationService.handleBloodRequest(orgId, request, false);
        if (handled) return ResponseEntity.ok(request);
        return ResponseEntity.status(500).body("Failed to reject request");
    }

    // List all events for an organization user
    @Autowired
    private bloodbank.bloodbank.repository.EventRepository eventRepository;

    @Autowired
    private bloodbank.bloodbank.repository.EventParticipantRepository eventParticipantRepository;

    @GetMapping("/{userId}/events")
    public ResponseEntity<List<bloodbank.bloodbank.entity.Event>> listEvents(@PathVariable Long userId) {
        var user = userService.getUser(userId);
        if (user == null || user.getUserRole() == null ||
            !(ROLE_ORGANIZATION.equalsIgnoreCase(user.getUserRole()) || ROLE_ORGANISATION.equalsIgnoreCase(user.getUserRole()))) {
            return ResponseEntity.status(403).build();
        }
        // Fetch events for this organization user
        List<bloodbank.bloodbank.entity.Event> events = eventRepository.findByUser_Id(userId);
        if (events == null) events = java.util.Collections.emptyList();
        return ResponseEntity.ok(events);
    }

    // Add a new event for an organization user
    @PostMapping("/{userId}/events")
    public ResponseEntity<?> addEvent(@PathVariable Long userId, @RequestBody bloodbank.bloodbank.entity.Event event) {
        bloodbank.bloodbank.entity.Event created = organizationService.createEvent(userId, event);
        if (created == null) return ResponseEntity.badRequest().body("User not found");
        return ResponseEntity.ok(created);
    }

    // Delete an event for an organization user
    @org.springframework.web.bind.annotation.DeleteMapping("/{userId}/events/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        var event = eventRepository.findById(eventId).orElse(null);
        if (event == null || event.getUser() == null || !event.getUser().getId().equals(userId)) {
            return ResponseEntity.notFound().build();
        }
        eventRepository.delete(event);
        return ResponseEntity.ok("Deleted");
    }

    // Update an event for an organization user
    @org.springframework.web.bind.annotation.PutMapping("/{userId}/events/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody bloodbank.bloodbank.entity.Event updatedEvent) {
        var event = eventRepository.findById(eventId).orElse(null);
        if (event == null || event.getUser() == null || !event.getUser().getId().equals(userId)) {
            return ResponseEntity.notFound().build();
        }
        
        // Update event fields
        event.setTitle(updatedEvent.getTitle());
        event.setEventDate(updatedEvent.getEventDate());
        event.setLocation(updatedEvent.getLocation());
        event.setDetails(updatedEvent.getDetails());
        
        bloodbank.bloodbank.entity.Event savedEvent = eventRepository.save(event);
        return ResponseEntity.ok(savedEvent);
    }

    // Get participants for an event
    @GetMapping("/{userId}/events/{eventId}/participants")
    public ResponseEntity<List<bloodbank.bloodbank.entity.EventParticipant>> getEventParticipants(@PathVariable Long userId, @PathVariable Long eventId) {
        var event = eventRepository.findById(eventId).orElse(null);
        if (event == null || event.getUser() == null || !event.getUser().getId().equals(userId)) {
            return ResponseEntity.notFound().build();
        }
        
        // Find all participants for this event
        List<bloodbank.bloodbank.entity.EventParticipant> participants = eventParticipantRepository.findByEventId(eventId);
        return ResponseEntity.ok(participants);
    }

    private static final String ROLE_ORGANIZATION = "ORGANIZATION";
    private static final String ROLE_ORGANISATION = "ORGANISATION";

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private bloodbank.bloodbank.config.JwtUtil jwtUtil;

    @Autowired
    private bloodbank.bloodbank.service.UserService userService;

    @Autowired
    private bloodbank.bloodbank.service.OrganizationService orgService;

    // LOGIN (role-based) — now returns JWT token for organization
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String normalized = request.getUsername() == null ? null : request.getUsername().trim().toLowerCase();

        // Require a real Organization record for organization login. Do not fall back to users table.
        var org = organizationService.findByUsername(normalized);
        if (org == null) {
            return ResponseEntity.status(400).body("Organization not found");
        }

        if (!passwordEncoder.matches(request.getPassword(), org.getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        // CRITICAL: Verify that the linked User has ORGANIZATION role
        if (org.getUser() != null) {
            String userRole = org.getUser().getUserRole();
            if (userRole == null || 
                !(ROLE_ORGANIZATION.equalsIgnoreCase(userRole) || ROLE_ORGANISATION.equalsIgnoreCase(userRole))) {
                return ResponseEntity.status(403).body("Access denied: User account does not have organization privileges");
            }
        } else {
            return ResponseEntity.status(400).body("Organization record is not linked to a valid user account");
        }

        Long userId = null;
        if (org.getUser() != null) userId = org.getUser().getId();

        String token = jwtUtil.generateToken(org.getUsername(), ROLE_ORGANIZATION);
        return ResponseEntity.ok(new LoginResponse(
            userId != null ? userId : org.getId(), 
            org.getUsername(), 
            ROLE_ORGANIZATION, 
            token, 
            org.getId(),
            org.getOrganizationId()
        ));
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    static class LoginResponse {
        private Long id; // User ID
        private String username;
        private String role;
        private String token;
        private Long organizationId; // Organization table ID
        private String organizationCustomId; // Custom organization ID (ORG-XXXXXX-YYYYMMDD)
    }

    @lombok.Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    // ... other endpoints for events, donations, requests
}