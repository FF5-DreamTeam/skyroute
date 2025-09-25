package com.skyroute.skyroute.flight.repository;

import com.skyroute.skyroute.flight.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    // =====================================================================
    // FLIGHT AVAILABILITY
    // =====================================================================

    /**
     * Available flights (available=true and in the future)
     */
    @Query("SELECT f FROM Flight f WHERE f.available = true AND f.departureTime > :now")
    List<Flight> findAvailableFlights(@Param("now") LocalDateTime now);

    /**
     * Available flights for a specific route
     */
    @Query("SELECT f FROM Flight f WHERE f.route.id = :routeId AND f.available = true AND f.departureTime > :now")
    List<Flight> findAvailableFlightsByRoute(@Param("routeId") Long routeId, @Param("now") LocalDateTime now);

    /**
     * Flights with available seats
     */
    @Query("SELECT f FROM Flight f WHERE f.availableSeats > 0 AND f.available = true AND f.departureTime > :now")
    List<Flight> findFlightsWithAvailableSeats(@Param("now") LocalDateTime now);

    /**
     * Flights requiring status update
     * (already departed or have no available seats)
     */
    @Query("SELECT f FROM Flight f WHERE (f.departureTime <= :now AND f.available = true) OR " +
            "(f.availableSeats = 0 AND f.available = true)")
    List<Flight> findFlightsRequiringStatusUpdate(@Param("now") LocalDateTime now);

    // =====================================================================
    // SEARCH BY ORIGIN AND DESTINATION
    // =====================================================================

    /**
     * Flexible: search flights by origin/destination city or code
     */
    @Query("SELECT f FROM Flight f WHERE " +
            "(f.route.origin.city = :origin OR f.route.origin.code = :origin) AND " +
            "(f.route.destination.city = :destination OR f.route.destination.code = :destination) AND " +
            "f.available = true AND f.departureTime > :now AND f.availableSeats > 0")
    List<Flight> findAvailableFlightsByOriginAndDestination(@Param("origin") String origin,
                                                            @Param("destination") String destination,
                                                            @Param("now") LocalDateTime now);

    // =====================================================================
    // SEARCH BY FLIGHT NUMBER
    // =====================================================================

    Optional<Flight> findByFlightNumber(String flightNumber);

    boolean existsByFlightNumber(String flightNumber);

    // =====================================================================
    // SEARCH BY AIRCRAFT
    // =====================================================================

    @Query("SELECT f FROM Flight f WHERE f.aircraft.id = :aircraftId")
    List<Flight> findByAircraftId(@Param("aircraftId") Long aircraftId);

    @Query("SELECT f FROM Flight f WHERE f.aircraft.model = :model")
    List<Flight> findByAircraftModel(@Param("model") String model);

    // =====================================================================
    // SEARCH BY DATES AND RANGES
    // =====================================================================

    @Query("SELECT f FROM Flight f WHERE f.departureTime BETWEEN :startDate AND :endDate")
    List<Flight> findFlightsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    @Query("SELECT f FROM Flight f WHERE f.departureTime BETWEEN :now AND :futureTime AND f.available = true")
    List<Flight> findFlightsInNextHours(@Param("now") LocalDateTime now,
                                        @Param("futureTime") LocalDateTime futureTime);

    // =====================================================================
    // ADVANCED FILTERS
    // =====================================================================

    /**
     * Advanced search with optional filters
     */
    @Query("SELECT f FROM Flight f WHERE " +
            "(:origin IS NULL OR f.route.origin.city = :origin OR f.route.origin.code = :origin) AND " +
            "(:destination IS NULL OR f.route.destination.city = :destination OR f.route.destination.code = :destination) AND " +
            "(:departureDate IS NULL OR FUNCTION('DATE', f.departureTime) = FUNCTION('DATE', :departureDate)) AND " +
            "(:minPrice IS NULL OR f.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR f.price <= :maxPrice) AND " +
            "(:minSeats IS NULL OR f.availableSeats >= :minSeats) AND " +
            "f.available = true AND f.departureTime > :now " +
            "ORDER BY f.price ASC")
    List<Flight> searchFlightsWithFilters(@Param("origin") String origin,
                                          @Param("destination") String destination,
                                          @Param("departureDate") LocalDateTime departureDate,
                                          @Param("minPrice") Double minPrice,
                                          @Param("maxPrice") Double maxPrice,
                                          @Param("minSeats") Integer minSeats,
                                          @Param("now") LocalDateTime now);

    /**
     * Find similar flights (same route, close date)
     */
    @Query("SELECT f FROM Flight f WHERE f.route.id = :routeId " +
            "AND f.departureTime BETWEEN :startRange AND :endRange " +
            "AND f.available = true AND f.id != :excludeFlightId " +
            "ORDER BY f.price ASC")
    List<Flight> findSimilarFlights(@Param("routeId") Long routeId,
                                    @Param("startRange") LocalDateTime startRange,
                                    @Param("endRange") LocalDateTime endRange,
                                    @Param("excludeFlightId") Long excludeFlightId);

    // =====================================================================
    // STATISTICS
    // =====================================================================

    @Query("SELECT COUNT(f) FROM Flight f WHERE f.available = true AND f.departureTime > :now")
    Long countAvailableFlights(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(f) FROM Flight f WHERE f.route.id = :routeId")
    Long countFlightsByRoute(@Param("routeId") Long routeId);

    @Query("SELECT MIN(f.price) FROM Flight f WHERE f.route.id = :routeId " +
            "AND f.available = true AND f.departureTime > :now")
    Optional<Double> findMinPriceByRoute(@Param("routeId") Long routeId, @Param("now") LocalDateTime now);

    @Query("SELECT f FROM Flight f WHERE f.route.id = :routeId " +
            "AND f.available = true AND f.departureTime > :now " +
            "ORDER BY f.price ASC")
    List<Flight> findCheapestFlightsByRoute(@Param("routeId") Long routeId, @Param("now") LocalDateTime now);

    // =====================================================================
    // ALERTS AND NOTIFICATIONS
    // =====================================================================

    @Query("SELECT f FROM Flight f WHERE f.departureTime BETWEEN :now AND :notificationTime AND f.available = true")
    List<Flight> findUpcomingFlights(@Param("now") LocalDateTime now,
                                     @Param("notificationTime") LocalDateTime notificationTime);

    @Query("SELECT f FROM Flight f WHERE f.availableSeats <= :threshold " +
            "AND f.availableSeats > 0 AND f.available = true AND f.departureTime > :now")
    List<Flight> findFlightsWithLowAvailability(@Param("threshold") Integer threshold,
                                                @Param("now") LocalDateTime now);

    @EntityGraph(attributePaths = {"aircraft", "route", "route.origin", "route.destination"})
    Page<Flight> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"aircraft", "route", "route.origin", "route.destination"})
    Page<Flight> findByAvailableTrue(Pageable pageable);

    @EntityGraph(attributePaths = {"aircraft", "route", "route.origin", "route.destination"})
    Optional<Flight> findById(Long id);
}
