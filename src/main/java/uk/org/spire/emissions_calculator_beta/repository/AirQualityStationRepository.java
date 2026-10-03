package uk.org.spire.emissions_calculator_beta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.org.spire.emissions_calculator_beta.entity.AirQualityStation;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirQualityStationRepository extends JpaRepository<AirQualityStation, Long> {

    Optional<AirQualityStation> findBySiteCode(String siteCode);

    boolean existsBySiteCode(String siteCode);

    List<AirQualityStation> findBySiteCodeIn(List<String> siteCodes);
}