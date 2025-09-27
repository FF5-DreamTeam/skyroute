package com.skyroute.skyroute.flight.service.publicapi;

import com.skyroute.skyroute.flight.dto.publicapi.FlightMapper;
import com.skyroute.skyroute.flight.dto.publicapi.FlightSearchRequest;
import com.skyroute.skyroute.flight.dto.publicapi.FlightSimpleResponse;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.flight.repository.FlightRepository;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightPublicServiceImpl implements FlightPublicService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;

    @Override
    public Page<FlightSimpleResponse> getFlightsPage(
            Pageable pageable,
            String origin,
            String destination,
            LocalDate departureDate,
            Double minPrice,
            Double maxPrice,
            Integer passengers
    ) {
        List<Flight> flights = searchFlightsByCriteria(
                origin, destination, departureDate, minPrice, maxPrice, passengers
        );

        return paginateFlights(flights, pageable);
    }

    @Override
    public Page<FlightSimpleResponse> searchFlights(FlightSearchRequest request, Pageable pageable) {
        List<Flight> flights = searchFlightsByCriteria(
                request.origin(),
                request.destination(),
                request.departureDate(),
                request.minPrice(),
                request.maxPrice(),
                request.passengers()
        );

        return paginateFlights(flights, pageable);
    }

    private List<Flight> searchFlightsByCriteria(
            String origin,
            String destination,
            LocalDate departureDate,
            Double minPrice,
            Double maxPrice,
            Integer passengers
    ) {
        LocalDateTime departureDateStart = null;
        LocalDateTime departureDateEnd = null;

        if (departureDate != null) {
            departureDateStart = departureDate.atStartOfDay();
            departureDateEnd = departureDate.atTime(LocalTime.MAX);
        }

        return flightRepository.searchFlightsWithFilters(
                origin,
                destination,
                departureDateStart,
                departureDateEnd,
                minPrice,
                maxPrice,
                passengers,
                LocalDateTime.now()
        );
    }

    private Page<FlightSimpleResponse> paginateFlights(List<Flight> flights, Pageable pageable) {
        flights.forEach(flight -> {
            if (flight.getAvailableSeats() <= 0) {
                flight.setAvailable(false);
            }
        });

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), flights.size());

        List<FlightSimpleResponse> results = flights.subList(start, end)
                .stream()
                .map(flightMapper::toSimpleResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(results, pageable, flights.size());
    }

    @Override
    public FlightSimpleResponse getFlightById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight with id not found: " + id));
        return flightMapper.toSimpleResponse(flight);
    }
}

