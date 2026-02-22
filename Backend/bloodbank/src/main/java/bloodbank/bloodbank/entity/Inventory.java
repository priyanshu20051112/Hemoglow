package bloodbank.bloodbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne; // Corrected import for Column
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to organization user (for orgs stored in users table)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "blood_group")
    private String bloodGroup;

    private int liters;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;
}
