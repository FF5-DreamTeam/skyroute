package com.skyroute.skyroute.flight.repository;

import com.skyroute.skyroute.flight.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    @Query("SELECT f FROM Flight f WHERE f.available = true AND f.departureTime > :now")
    List<Flight> findAvailableFlights(@Param("now") LocalDateTime now);

    @Query("SELECT f FROM Flight f WHERE f.route.id = :routeId AND f.available = true AND f.departureTime > :now")
    List<Flight> findAvailableFlightsByRoute(@Param("routeId") Long routeId, @Param("now") LocalDateTime now);

    Optional<Flight> findByFlightNumber(String flightNumber);

    @Query("SELECT f From Flight f WHERE f.departureTime < :now AND f.available = true")
    List<Flight> findExpiredFlights(@Param("now") LocalDateTime now);

    @Query("SELECT f FROM Flight f WHERE f.availableSeats > 0 AND f.available = true AND f.departureTime > :now")
    List<Flight> findFlightsWithAvailableSeats(@Param("now") LocalDateTime now);

    @Query("SELECT f FROM Flight f WHERE f.aircraft.id = :aircraftId")
    List<Flight> findByAircraftId(@Param("aircraftId") Long aircraftId);

    @Query("SELECT f FROM Flight f WHERE f.departureTime BETWEEN :startDate AND :endDate")
    List<Flight> findFlightsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

    boolean existsByFlightNumber(String flightNumber);

    @Query("SELECT f FROM Flight f WHERE f.route.origin.city = :origin AND f.route.destination.city = :destination " +
            "AND f.available = true AND f.departureTime > :now AND f.availableSeats > 0")
    List<Flight> findAvailableFlightsByOriginAndDestination(@Param("origin") String origin,
                                                            @Param("destination") String destination,
                                                            @Param("now") LocalDateTime now);
}

