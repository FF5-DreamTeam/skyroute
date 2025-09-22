package com.skyroute.skyroute.flight.repository;

import com.skyroute.skyroute.flight.entity.Flight;
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
    // DISPONIBILIDAD DE VUELOS
    // =====================================================================

    /**
     * Vuelos disponibles (available=true y en el futuro)
     */
    @Query("SELECT f FROM Flight f WHERE f.available = true AND f.departureTime > :now")
    List<Flight> findAvailableFlights(@Param("now") LocalDateTime now);

    /**
     * Vuelos disponibles por ruta específica
     */
    @Query("SELECT f FROM Flight f WHERE f.route.id = :routeId AND f.available = true AND f.departureTime > :now")
    List<Flight> findAvailableFlightsByRoute(@Param("routeId") Long routeId, @Param("now") LocalDateTime now);

    /**
     * Vuelos con asientos disponibles
     */
    @Query("SELECT f FROM Flight f WHERE f.availableSeats > 0 AND f.available = true AND f.departureTime > :now")
    List<Flight> findFlightsWithAvailableSeats(@Param("now") LocalDateTime now);

    /**
     * Vuelos que requieren actualización de estado
     * (ya salieron o no tienen asientos disponibles)
     */
    @Query("SELECT f FROM Flight f WHERE (f.departureTime <= :now AND f.available = true) OR " +
            "(f.availableSeats = 0 AND f.available = true)")
    List<Flight> findFlightsRequiringStatusUpdate(@Param("now") LocalDateTime now);

    // =====================================================================
    // BÚSQUEDA POR ORIGEN Y DESTINO
    // =====================================================================

    /**
     * Flexible: busca vuelos por ciudad o código de origen/destino
     */
    @Query("SELECT f FROM Flight f WHERE " +
            "(f.route.origin.city = :origin OR f.route.origin.code = :origin) AND " +
            "(f.route.destination.city = :destination OR f.route.destination.code = :destination) AND " +
            "f.available = true AND f.departureTime > :now AND f.availableSeats > 0")
    List<Flight> findAvailableFlightsByOriginAndDestination(@Param("origin") String origin,
                                                            @Param("destination") String destination,
                                                            @Param("now") LocalDateTime now);

    // =====================================================================
    // BÚSQUEDA POR NÚMERO DE VUELO
    // =====================================================================

    Optional<Flight> findByFlightNumber(String flightNumber);

    boolean existsByFlightNumber(String flightNumber);

    // =====================================================================
    // BÚSQUEDA POR AERONAVE
    // =====================================================================

    @Query("SELECT f FROM Flight f WHERE f.aircraft.id = :aircraftId")
    List<Flight> findByAircraftId(@Param("aircraftId") Long aircraftId);

    @Query("SELECT f FROM Flight f WHERE f.aircraft.model = :model")
    List<Flight> findByAircraftModel(@Param("model") String model);

    // =====================================================================
    // BÚSQUEDA POR FECHAS Y RANGOS
    // =====================================================================

    @Query("SELECT f FROM Flight f WHERE f.departureTime BETWEEN :startDate AND :endDate")
    List<Flight> findFlightsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    @Query("SELECT f FROM Flight f WHERE f.departureTime BETWEEN :now AND :futureTime AND f.available = true")
    List<Flight> findFlightsInNextHours(@Param("now") LocalDateTime now,
                                        @Param("futureTime") LocalDateTime futureTime);

    // =====================================================================
    // FILTROS AVANZADOS
    // =====================================================================

    /**
     * Búsqueda avanzada con filtros opcionales
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
     * Buscar vuelos similares (misma ruta, fecha cercana)
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
    // ESTADÍSTICAS
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
    // ALERTAS Y NOTIFICACIONES
    // =====================================================================

    @Query("SELECT f FROM Flight f WHERE f.departureTime BETWEEN :now AND :notificationTime AND f.available = true")
    List<Flight> findUpcomingFlights(@Param("now") LocalDateTime now,
                                     @Param("notificationTime") LocalDateTime notificationTime);

    @Query("SELECT f FROM Flight f WHERE f.availableSeats <= :threshold " +
            "AND f.availableSeats > 0 AND f.available = true AND f.departureTime > :now")
    List<Flight> findFlightsWithLowAvailability(@Param("threshold") Integer threshold,
                                                @Param("now") LocalDateTime now);
}


