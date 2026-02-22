package bloodbank.bloodbank.controller;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bloodbank.bloodbank.entity.Donation;
import bloodbank.bloodbank.entity.Event;
import bloodbank.bloodbank.entity.EventParticipant;
import bloodbank.bloodbank.entity.Request;
import bloodbank.bloodbank.entity.User;
import bloodbank.bloodbank.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserDashboardController {

    private final UserService userService;
    private final bloodbank.bloodbank.service.OrganizationService organizationService;

    public UserDashboardController(UserService userService, bloodbank.bloodbank.service.OrganizationService organizationService){
        this.userService = userService;
        this.organizationService = organizationService;
    }

    // Profile
    @GetMapping("/dashboard/{id}")
    public ResponseEntity<User> getProfile(@PathVariable Long id){
        User user = userService.getUser(id);
        if(user==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateProfile(@PathVariable Long id, @RequestBody User updatedData){
        try {
            User updatedUser = userService.updateUser(id, updatedData);
            if(updatedUser == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedUser);
        } catch(Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Requests
    @GetMapping("/{id}/requests")
    public ResponseEntity<List<Request>> getRequests(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserRequests(id));
    }

    @PostMapping("/{id}/requests")
    public ResponseEntity<?> addRequest(@PathVariable Long id, @RequestBody Request request, java.security.Principal principal){
        System.out.println("DEBUG: Incoming request payload: patient=" + request.getPatient() + ", bloodGroup=" + request.getBloodGroup() + ", hospital=" + request.getHospital() + ", phone=" + request.getPhone() + ", amount=" + request.getAmount());
        System.out.println("DEBUG: Organization in request: " + (request.getOrganization() != null ? request.getOrganization().getId() : "null"));
        
        User user = userService.getUser(id);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        request.setUser(user);
        request.setStatus("PENDING"); // Set initial status
        
        // Determine organization to attach
        if (request.getOrganization() != null && request.getOrganization().getId() != null) {
            // Client provided an organization id — this is a request FROM a user TO an organization
            Long orgUserId = request.getOrganization().getId();
            System.out.println("DEBUG: Looking for organization with User ID: " + orgUserId);
            
            // The frontend sends User ID, but we need to find the Organization by User ID
            var org = organizationService.findByUserId(orgUserId);
            if (org == null) {
                System.out.println("DEBUG: Organization not found for User ID: " + orgUserId);
                return ResponseEntity.badRequest().body("Organization not found");
            }
            
            // For regular users sending requests to organizations, just set the organization
            // No ownership validation needed - any user can send a request to any organization
            request.setOrganization(org);
            System.out.println("DEBUG: Organization set successfully: " + org.getName());
            
        } else {
            System.out.println("DEBUG: No organization provided in request");
            // No org id provided - check if the requester is an organization creating internal request
            if (user.getUserRole() != null && "ORGANIZATION".equalsIgnoreCase(user.getUserRole())) {
                var org = organizationService.findByUsername(user.getUsername());
                if (org != null) {
                    request.setOrganization(org);
                } else {
                    // Organization user but no organization found - this shouldn't happen
                    return ResponseEntity.badRequest().body("Organization not found for organization user");
                }
            } else {
                // Regular user without specifying organization - not allowed
                return ResponseEntity.badRequest().body("Organization must be specified for user requests");
            }
        }
        
        return ResponseEntity.ok(userService.addRequest(request));
    }

    // Donations
    @GetMapping("/{id}/donations")
    public ResponseEntity<List<Donation>> getDonations(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserDonations(id));
    }

    @PostMapping("/{id}/donations")
    public ResponseEntity<Donation> addDonation(@PathVariable Long id, @RequestBody Donation donation){
        User user = userService.getUser(id);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        donation.setUser(user);
        return ResponseEntity.ok(userService.addDonation(donation));
    }

    // Events
    @GetMapping("/events")
    public ResponseEntity<List<Event>> getEvents(){ 
        return ResponseEntity.ok(userService.getAllEvents()); 
    }

    @PostMapping("/{userId}/events/{eventId}/join")
    public ResponseEntity<String> joinEvent(@PathVariable Long userId, @PathVariable Long eventId){
        boolean joined = userService.joinEvent(userId, eventId);
        return ResponseEntity.ok(joined ? "Joined successfully" : "Already joined");
    }

    @GetMapping("/{userId}/events")
    public ResponseEntity<List<EventParticipant>> getUserEvents(@PathVariable Long userId){
        return ResponseEntity.ok(userService.getUserEventParticipants(userId));
    }
}
