package bloodbank.bloodbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bloodbank.bloodbank.entity.Organization;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Organization findByUsername(String username);
    Organization findByUsernameIgnoreCase(String username);
    Organization findByUser_Id(Long userId);
}
