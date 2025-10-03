package com.skyroute.skyroute.flight.repository;

import com.skyroute.skyroute.flight.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long>, JpaSpecificationExecutor<Flight> {
  @EntityGraph(attributePaths = { "aircraft", "route", "route.origin", "route.destination" })
  Page<Flight> findAll(Pageable pageable);

  @EntityGraph(attributePaths = { "aircraft", "route", "route.origin", "route.destination" })
  Optional<Flight> findById(Long id);

  @Query("""
          SELECT r.destination.code, r.destination.city, MIN(f.price)
          FROM Flight f
          JOIN f.route r
          WHERE r.destination.code IN :destinationCodes
            AND f.available = true
            AND f.departureTime > CURRENT_TIMESTAMP
          GROUP BY r.destination.code, r.destination.city
          ORDER BY r.destination.code
      """)
  List<Object[]> findMinPricesByDestinations(@Param("destinationCodes") List<String> destinationCodes);
}