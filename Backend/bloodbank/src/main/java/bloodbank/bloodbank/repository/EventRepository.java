package bloodbank.bloodbank.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bloodbank.bloodbank.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
	java.util.List<Event> findByUser_Id(Long userId);
}
