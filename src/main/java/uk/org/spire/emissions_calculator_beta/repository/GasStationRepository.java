package uk.org.spire.emissions_calculator_beta.repository;

import uk.org.spire.emissions_calculator_beta.entity.GasStation;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GasStationRepository extends JpaRepository<GasStation, Long> {

    Optional<GasStation> findByExternalId(String externalId);

    // Consulta espacial nativa PostGIS (distância em metros usando o esferoide SRID 4326)
    @Query(value = "SELECT * FROM gas_stations g WHERE ST_DWithin(g.geometry, CAST(:location AS geometry), :radiusInMeters, true)", nativeQuery = true)
    List<GasStation> findStationsWithinRadius(
            @Param("location") Point location,
            @Param("radiusInMeters") double radiusInMeters
    );
}