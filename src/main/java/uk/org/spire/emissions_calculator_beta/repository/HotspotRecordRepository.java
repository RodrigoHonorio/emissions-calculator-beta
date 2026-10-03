package uk.org.spire.emissions_calculator_beta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.org.spire.emissions_calculator_beta.entity.HotspotRecord;
import uk.org.spire.emissions_calculator_beta.constants.SeverityLevel;
import java.util.List;

@Repository
public interface HotspotRecordRepository extends JpaRepository<HotspotRecord, Long> {
    List<HotspotRecord> findByDailySnapshotId(Long dailySnapshotId);
    List<HotspotRecord> findByDailySnapshotIdAndSeverityLevel(Long dailySnapshotId, SeverityLevel severityLevel);
}