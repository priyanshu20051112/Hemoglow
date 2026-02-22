package bloodbank.bloodbank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import bloodbank.bloodbank.entity.EventParticipant;

@Repository
public interface EventParticipantRepository extends JpaRepository<EventParticipant, Long> {
    List<EventParticipant> findByUserId(Long userId);
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
    List<EventParticipant> findByEventId(Long eventId);
}
