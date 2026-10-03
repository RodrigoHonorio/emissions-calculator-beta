package uk.org.spire.emissions_calculator_beta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.org.spire.emissions_calculator_beta.entity.MeteorologicalMeasurement;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface MeteorologicalMeasurementRepository extends JpaRepository<MeteorologicalMeasurement, Long> {

    List<MeteorologicalMeasurement> findByObservationTimestampBetween(
            OffsetDateTime start, OffsetDateTime end
    );
}