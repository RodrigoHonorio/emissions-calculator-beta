package uk.org.spire.emissions_calculator_beta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.org.spire.emissions_calculator_beta.entity.AirQualityMeasurement;
import uk.org.spire.emissions_calculator_beta.entity.AirQualityStation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AirQualityMeasurementRepository extends JpaRepository<AirQualityMeasurement, Long> {

    // Método para o Backfill verificar se a medição já existe e evitar duplicatas
    boolean existsByStationAndMeasuredAt(AirQualityStation station, LocalDateTime measuredAt);

    // Método para descobrir qual foi a última medição registrada de uma estação específica (mantido caso seja útil noutros locais)
    @Query("SELECT MAX(m.measuredAt) FROM AirQualityMeasurement m WHERE m.station = :station")
    Optional<LocalDateTime> findLatestMeasurementDateByStation(@Param("station") AirQualityStation station);

    // Novo método para buscar a última medição de todas as estações de uma só vez (Evita o problema N+1)
    @Query("SELECT m.station.id, MAX(m.measuredAt) FROM AirQualityMeasurement m GROUP BY m.station.id")
    List<Object[]> findLatestTimestampsPerStation();
}