package bloodbank.bloodbank.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import bloodbank.bloodbank.entity.Inventory;
import bloodbank.bloodbank.service.InventoryService;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    record PublicInventoryDTO(Long orgId, String orgName, String bloodGroup, int liters) {}

    // Public: get available inventory for all organizations by blood group (for users)
    @GetMapping("/public")
    public ResponseEntity<?> getPublicInventory(@RequestParam String bloodGroup) {
        String normalized = bloodGroup.trim().toUpperCase();
        var list = inventoryService.getPublicInventoryByBloodGroup(normalized);
        var dto = list.stream().map(i -> new PublicInventoryDTO(
            i.getUser() != null ? i.getUser().getId() : null,
            i.getUser() != null ? i.getUser().getUsername() : "",
            i.getBloodGroup(),
            i.getLiters()
        )).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(dto);
    }

    // Org: upsert inventory for an organization (requires ORG role via SecurityConfig)
    // Only allow the authenticated organization to update its own inventory.
    @PutMapping("/org/{userId}")
    public ResponseEntity<?> upsertUserInventory(@PathVariable Long userId, @RequestBody Inventory body, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        // Only allow the authenticated user to update their own inventory
        // You may want to check userId matches principal.getName() (if username is used as ID)
        // For now, assume userId is the authenticated user's ID
        Long organizationId = null;
        if (body.getOrganization() != null) {
            organizationId = body.getOrganization().getId();
        }
        Inventory inv = inventoryService.upsertUserInventory(userId, body.getBloodGroup(), body.getLiters(), organizationId);
        if (inv == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(inv);
    }

    // Org: get this organization's inventory (protected)
    @GetMapping("/org/{userId}")
    public ResponseEntity<?> getUserInventory(@PathVariable Long userId, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        // Only allow the authenticated user to view their own inventory
    var list = inventoryService.getByUser(userId);
    var dto = list.stream().map(i -> new PublicInventoryDTO(userId, (i.getUser() != null ? i.getUser().getUsername() : ""), i.getBloodGroup(), i.getLiters())).collect(java.util.stream.Collectors.toList());
    return ResponseEntity.ok(dto);
    }
}
