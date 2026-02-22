package bloodbank.bloodbank.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bloodbank.bloodbank.entity.Inventory;
import bloodbank.bloodbank.entity.Organization;
import bloodbank.bloodbank.repository.InventoryRepository;
import bloodbank.bloodbank.repository.OrganizationRepository;
import bloodbank.bloodbank.repository.UserRepository;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationRepository organizationRepository;

    public List<Inventory> getPublicInventoryByBloodGroup(String bloodGroup) {
    String normalized = normalizeBloodGroup(bloodGroup);
    System.out.println("Normalized bloodGroup: " + normalized);
    List<Inventory> result = inventoryRepository.findByBloodGroupNormalized(normalized);
    System.out.println("Query result: " + result);
    return result;
    }

    private String normalizeBloodGroup(String bg) {
    if (bg == null) return "";
    return bg.trim().toUpperCase().replaceAll("\\s+", "");
    }

    public List<Inventory> getByUser(Long userId) {
        return inventoryRepository.findByUserId(userId);
    }

    public Inventory upsertUserInventory(Long userId, String bloodGroup, int liters, Long organizationId) {
        bloodbank.bloodbank.entity.User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;
        Organization organization = null;
        if (organizationId != null) {
            organization = organizationRepository.findById(organizationId).orElse(null);
        }
        List<Inventory> existing = inventoryRepository.findByUserId(userId).stream()
                .filter(i -> i.getBloodGroup().equalsIgnoreCase(bloodGroup))
                .collect(Collectors.toList());
        Inventory inv;
        if (!existing.isEmpty()) {
            inv = existing.get(0);
            inv.setLiters(liters);
            inv.setOrganization(organization);
        } else {
            inv = Inventory.builder()
                .user(user)
                .bloodGroup(bloodGroup.toUpperCase())
                .liters(liters)
                .organization(organization)
                .build();
        }
        return inventoryRepository.save(inv);
    }

}
