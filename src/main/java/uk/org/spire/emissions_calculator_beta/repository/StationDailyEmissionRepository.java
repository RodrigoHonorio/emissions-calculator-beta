package uk.org.spire.emissions_calculator_beta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.org.spire.emissions_calculator_beta.entity.StationDailyEmission;

import java.util.List;

@Repository
public interface StationDailyEmissionRepository extends JpaRepository<StationDailyEmission, Long> {
    List<StationDailyEmission> findByDailySnapshotId(Long dailySnapshotId);
}