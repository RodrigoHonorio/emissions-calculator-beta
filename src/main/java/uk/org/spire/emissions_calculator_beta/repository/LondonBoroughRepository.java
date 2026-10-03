package uk.org.spire.emissions_calculator_beta.repository;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.org.spire.emissions_calculator_beta.entity.LondonBorough;

import java.util.Optional;

@Repository
public interface LondonBoroughRepository extends JpaRepository<LondonBorough, Long> {

    Optional<LondonBorough> findByCode(String code);

    // Encontra a que borough pertence um determinado ponto geográfico
    @Query(value = "SELECT * FROM london_boroughs b WHERE ST_Contains(b.boundary, CAST(:location AS geometry)) LIMIT 1", nativeQuery = true)
    Optional<LondonBorough> findBoroughContainingPoint(@Param("location") Point location);
}