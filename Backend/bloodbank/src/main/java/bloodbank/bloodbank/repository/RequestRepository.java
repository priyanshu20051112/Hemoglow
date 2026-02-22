package bloodbank.bloodbank.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bloodbank.bloodbank.entity.Request;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long>{
    List<Request>findByUserId(Long userId);
    List<Request>findByOrganizationId(Long organizationId);
}
