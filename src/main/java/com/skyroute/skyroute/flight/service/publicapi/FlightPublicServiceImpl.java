package com.skyroute.skyroute.flight.service.publicapi;

import com.skyroute.skyroute.flight.dto.admin.FlightRequest;
import com.skyroute.skyroute.flight.dto.publicapi.FlightSearchRequest;
import com.skyroute.skyroute.flight.dto.publicapi.FlightSimpleResponse;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.flight.repository.FlightRepository;
import com.skyroute.skyroute.flight.service.admin.FlightService;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightPublicServiceImpl implements FlightPublicService {

    private final FlightRepository flightRepository;
    private final FlightService flightService;

    @Override
    public List<FlightSimpleResponse> searchFlights(FlightSearchRequest request) {
        LocalDateTime now = LocalDateTime.now();

        List<Flight> flights = flightRepository.searchFlightsWithFilters(
                request.origin(),
                request.destination(),
                request.departureDate(),
                request.minPrice(),
                request.maxPrice(),
                request.passengers(),
                now
        );

        return flights.stream()
                .map(this::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FlightSimpleResponse getFlightById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight with id:  not found" + id));
        return toSimpleResponse(flight);
    }

    @Override
    public Flight findEntityById(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight with id:  not found" + id));
    }

    private FlightSimpleResponse toSimpleResponse(Flight flight) {
        return new FlightSimpleResponse(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getAvailableSeats(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getPrice(),
                flight.isAvailable(),
                flight.getAircraft() != null ? flight.getAircraft().getModel() : null,
                flight.getRoute() != null && flight.getRoute().getOrigin() != null
                        ? flight.getRoute().getOrigin().getCity() : null,
                flight.getRoute() != null && flight.getRoute().getDestination() != null
                        ? flight.getRoute().getDestination().getCity() : null
        );
    }

    @Transactional
    public FlightSimpleResponse reserveFirstAlternative(Long originalFlightId, int passengers) {
        Flight originalFlight = findEntityById(originalFlightId);

        List<FlightSimpleResponse> alternatives = findAlternativeFlights(originalFlightId, passengers);

        // Ensure a chosen alternative exists
        if (alternatives.isEmpty()) {
            throw new EntityNotFoundException("No alternative flights available for the selected route and number of passengers");
        }

        FlightSimpleResponse chosen = alternatives.get(0);
        Flight flightEntity = flightService.findEntityById(chosen.id());

        // Calcular precio según regla de 2 horas
        double priceToPay;
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(originalFlight.getDepartureTime().plusHours(2))) {
            priceToPay = Math.max(chosen.price() - originalFlight.getPrice(), 0.0);
        } else {
            priceToPay = chosen.price();
        }

        // Reducir asientos disponibles
        flightEntity.setAvailableSeats(flightEntity.getAvailableSeats() - passengers);

        // Get related entity IDs with null checks to prevent NullPointerException
        Long aircraftId = (flightEntity.getAircraft() != null) ? flightEntity.getAircraft().getId() : null;
        Long routeId = (flightEntity.getRoute() != null) ? flightEntity.getRoute().getId() : null;

        flightService.updateFlight(flightEntity.getId(), FlightRequest.builder()
                .flightNumber(flightEntity.getFlightNumber())
                .availableSeats(flightEntity.getAvailableSeats())
                .departureTime(flightEntity.getDepartureTime())
                .arrivalTime(flightEntity.getArrivalTime())
                .price(flightEntity.getPrice())
                .aircraftId(aircraftId)
                .routeId(routeId)
                .available(flightEntity.isAvailable())
                .build()
        );

        // Devolver DTO con precio calculado
        return new FlightSimpleResponse(
                flightEntity.getId(),
                flightEntity.getFlightNumber(),
                flightEntity.getAvailableSeats(),
                flightEntity.getDepartureTime(),
                flightEntity.getArrivalTime(),
                priceToPay,
                flightEntity.isAvailable(),
                flightEntity.getAircraft() != null ? flightEntity.getAircraft().getModel() : null,
                flightEntity.getRoute() != null && flightEntity.getRoute().getOrigin() != null
                        ? flightEntity.getRoute().getOrigin().getCity() : null,
                flightEntity.getRoute() != null && flightEntity.getRoute().getDestination() != null
                        ? flightEntity.getRoute().getDestination().getCity() : null
        );
    }

    private List<FlightSimpleResponse> findAlternativeFlights(Long originalFlightId, int passengers) {
        Flight originalFlight = findEntityById(originalFlightId);

        LocalDateTime startRange = originalFlight.getDepartureTime().minusHours(6);
        LocalDateTime endRange = originalFlight.getDepartureTime().plusHours(6);

        List<Flight> alternatives = flightRepository.findSimilarFlights(
                originalFlight.getRoute().getId(),
                startRange,
                endRange,
                originalFlightId
        );

        // Create a mutable copy of the list before filtering to avoid UnsupportedOperationException
        List<Flight> mutableAlternatives = new ArrayList<>(alternatives);

        mutableAlternatives.removeIf(f -> f.getAvailableSeats() < passengers);

        if (mutableAlternatives.isEmpty()) {
            throw new EntityNotFoundException("No alternative flights available for the selected route and number of passengers");
        }

        return mutableAlternatives.stream()
                .map(this::toSimpleResponse)
                .collect(Collectors.toList());
    }
}