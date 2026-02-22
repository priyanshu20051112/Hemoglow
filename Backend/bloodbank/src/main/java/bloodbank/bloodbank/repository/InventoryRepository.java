package bloodbank.bloodbank.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import bloodbank.bloodbank.entity.Inventory;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Query("SELECT i FROM Inventory i WHERE UPPER(REPLACE(i.bloodGroup, ' ', '')) = :bloodGroup")
    List<Inventory> findByBloodGroupNormalized(@Param("bloodGroup") String bloodGroup);
    List<Inventory> findByUserId(Long userId);
}
