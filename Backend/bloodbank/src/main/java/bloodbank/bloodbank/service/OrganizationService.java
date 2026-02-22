package bloodbank.bloodbank.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bloodbank.bloodbank.entity.Event;
import bloodbank.bloodbank.entity.Organization;
import bloodbank.bloodbank.entity.Request;

@Service
public class OrganizationService {
    private final bloodbank.bloodbank.repository.EventParticipantRepository eventParticipantRepository;
    private final bloodbank.bloodbank.repository.OrganizationRepository organizationRepository;
    private final bloodbank.bloodbank.repository.InventoryRepository inventoryRepository;
    private final bloodbank.bloodbank.repository.UserRepository userRepository;
    private final bloodbank.bloodbank.repository.EventRepository eventRepository;
    private final bloodbank.bloodbank.repository.RequestRepository requestRepository;

    public OrganizationService(
        bloodbank.bloodbank.repository.EventParticipantRepository eventParticipantRepository,
        bloodbank.bloodbank.repository.OrganizationRepository organizationRepository,
        bloodbank.bloodbank.repository.InventoryRepository inventoryRepository,
        bloodbank.bloodbank.repository.UserRepository userRepository,
        bloodbank.bloodbank.repository.EventRepository eventRepository,
        bloodbank.bloodbank.repository.RequestRepository requestRepository
    ) {
        this.eventParticipantRepository = eventParticipantRepository;
        this.organizationRepository = organizationRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
    }

    // Get participants for a given event
    public List<bloodbank.bloodbank.entity.EventParticipant> getEventParticipants(Long eventId) {
        return eventParticipantRepository.findAll().stream()
            .filter(ep -> ep.getEvent().getId().equals(eventId))
            .toList();
    }
    // Return all organizations
    public List<Organization> findAll() {
        return organizationRepository.findAll();
    }


    // ...existing code...

    // Find org by username
    public Organization findByUsername(String username) {
        Organization org = organizationRepository.findByUsernameIgnoreCase(username);
        if (org == null) return organizationRepository.findByUsername(username);
        return org;
    }

    // Find org by ID
    public Organization findById(Long id) {
        return organizationRepository.findById(id).orElse(null);
    }

    // Find org by User ID (for organizations linked to user accounts)
    public Organization findByUserId(Long userId) {
        return organizationRepository.findByUser_Id(userId);
    }

    // Get blood liters available
    public int getBloodLiters(Long orgId) {
        Organization org = findById(orgId);
        if(org != null) return org.getBloodLiters();
        return 0;
    }

    // Update blood liters
    public void updateBloodLiters(Long orgId, int liters) {
        Organization org = findById(orgId);
        if(org != null){
            org.setBloodLiters(liters);
            organizationRepository.save(org);
        }
    }

    // Accept or reject blood request
    @Transactional
    public boolean handleBloodRequest(Long orgId, Request request, boolean accept) {
        Organization org = findById(orgId);
        if(org != null){
            if(accept){
                // Find inventory for this org and blood group
                List<bloodbank.bloodbank.entity.Inventory> invList = inventoryRepository.findByBloodGroupNormalized(request.getBloodGroup());
                bloodbank.bloodbank.entity.Inventory inventory = null;
                
                // First, look for inventory directly linked to the organization
                for (bloodbank.bloodbank.entity.Inventory inv : invList) {
                    if (inv.getOrganization() != null && inv.getOrganization().getId().equals(orgId)) {
                        inventory = inv;
                        break;
                    }
                }
                
                // If no organization-linked inventory found, look for inventory owned by the organization's user
                if (inventory == null && org.getUser() != null) {
                    for (bloodbank.bloodbank.entity.Inventory inv : invList) {
                        if (inv.getUser() != null && inv.getUser().getId().equals(org.getUser().getId())) {
                            inventory = inv;
                            // Link this inventory to the organization for future use
                            inventory.setOrganization(org);
                            inventoryRepository.save(inventory);
                            break;
                        }
                    }
                }
                
                if (inventory != null) {
                    int available = inventory.getLiters();
                    double requested = request.getAmount();
                    if(requested <= available){
                        inventory.setLiters((int)(available - requested));
                        inventoryRepository.save(inventory);
                        request.setStatus("ACCEPTED");
                    } else {
                        request.setStatus("REJECTED");
                    }
                } else {
                    request.setStatus("REJECTED");
                }
            } else {
                request.setStatus("REJECTED");
            }
            // persist the updated request status
            requestRepository.save(request);
            organizationRepository.save(org);
            return true;
        }
        return false;
    }

    // Create new event
    public Event createEvent(Long userId, Event event) {
        bloodbank.bloodbank.entity.User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            // Find the organization linked to this user
            Organization organization = organizationRepository.findByUser_Id(userId);
            if (organization != null) {
                event.setUser(user);
                event.setOrganization(organization);
                eventRepository.save(event);
                return event;
            }
        }
        return null;
    }

    // Open/close for donations
    public void setOpenForDonations(Long orgId, boolean open) {
        Organization org = findById(orgId);
        if(org != null){
            org.setOpenForDonations(open);
            organizationRepository.save(org);
        }
    }

    // Get all requests for the organization
    public List<Request> getAllRequests(Long orgId){
        Organization org = findById(orgId);
        if(org != null) return org.getRequests();
        return null;
    }

    // Public listing of organizations (lightweight DTO)
    public static record OrgPublicDTO(Long id, String name, int bloodLiters, boolean openForDonations) {}

    public List<OrgPublicDTO> getAllPublicOrgs() {
        List<Organization> all = organizationRepository.findAll();
        return all.stream()
                .map(o -> new OrgPublicDTO(o.getId(), o.getName(), o.getBloodLiters(), o.isOpenForDonations()))
                .toList();
    }

    // Update organization profile (partial update of allowed fields)
    public Organization updateProfile(Long id, Organization profile) {
        Organization org = findById(id);
        if (org == null) return null;
        if (profile.getName() != null) org.setName(profile.getName());
        if (profile.getEmail() != null) org.setEmail(profile.getEmail());
        if (profile.getPhone() != null) org.setPhone(profile.getPhone());
        if (profile.getAddress() != null) org.setAddress(profile.getAddress());
        return organizationRepository.save(org);
    }

    // Save a new organization (used for testing / registration)
    public Organization saveOrganization(Organization org) {
        return organizationRepository.save(org);
    }
}
