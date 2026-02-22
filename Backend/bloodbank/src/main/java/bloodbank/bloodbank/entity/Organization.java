package bloodbank.bloodbank.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_id", unique = true, nullable = false)
    private String organizationId; // Custom unique organization identifier

    private String name;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String address;

    private boolean openForDonations;

    private int bloodLiters; // total blood available

    // Link back to the User account for this organization (if organizations are also users)
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // ...removed events mapping, now events are owned by User...

    // Organization → Requests (OneToMany)
    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    @com.fasterxml.jackson.annotation.JsonIgnore
    @Builder.Default
    private List<Request> requests = new ArrayList<>();
}
