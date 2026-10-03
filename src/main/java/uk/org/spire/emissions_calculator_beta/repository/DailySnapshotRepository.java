package uk.org.spire.emissions_calculator_beta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.org.spire.emissions_calculator_beta.entity.DailySnapshot;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailySnapshotRepository extends JpaRepository<DailySnapshot, Long> {
    Optional<DailySnapshot> findBySnapshotDate(LocalDate snapshotDate);
}