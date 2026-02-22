package bloodbank.bloodbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient", nullable = false)
    private String patient;

    @Column(nullable = false)
    private String bloodGroup;  

    @Column(nullable = false)
    private String hospital;

    @Column(name = "contact_number", nullable = false)
    private String phone;

    // Link to User (who created the request)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Link to Organization (which can accept/reject)
    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;


    @Column(nullable = false)
    private Double amount; // Amount of blood requested

    @Column
    private String status; // PENDING / ACCEPTED / REJECTED

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPatient() { return patient; }
    public void setPatient(String patient) { this.patient = patient; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Organization getOrganization() { return organization; }
    public void setOrganization(Organization organization) { this.organization = organization; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
