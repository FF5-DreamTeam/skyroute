package com.skyroute.skyroute.flight.service.publicapi;

import com.skyroute.skyroute.flight.dto.publicapi.FlightMapper;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightPublicServiceImpl implements FlightPublicService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;

    @Override
    public Page<FlightSimpleResponse> searchFlightsByParams(
            String origin,
            String destination,
            String departureDate,
            String returnDate,
            Integer passengers,
            Pageable pageable
    ) {
        LocalDateTime departureDateStart = null;
        LocalDateTime departureDateEnd = null;

        if (departureDate != null && !departureDate.isBlank()) {
            departureDateStart = LocalDate.parse(departureDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
            departureDateEnd = LocalDate.parse(departureDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atTime(LocalTime.MAX);
        }

        List<Flight> flights = flightRepository.searchFlightsWithFilters(
                origin,
                destination,
                departureDateStart,
                departureDateEnd,
                null,   // minPrice
                null,   // maxPrice
                passengers,
                LocalDateTime.now()
        );

        return paginateFlights(flights, pageable);
    }

    @Override
    public Page<FlightSimpleResponse> searchFlightsByBudget(Double budget, Pageable pageable) {
        List<Flight> flights = flightRepository.searchFlightsWithFilters(
                null,       // origin
                null,       // destination
                null,       // departureDateStart
                null,       // departureDateEnd
                null,       // minPrice
                budget,     // maxPrice
                null,       // passengers
                LocalDateTime.now()
        );

        if (flights.isEmpty()) {
            return Page.empty(pageable);
        }

        return paginateFlights(flights, pageable);
    }

    private Page<FlightSimpleResponse> paginateFlights(List<Flight> flights, Pageable pageable) {
        flights.forEach(flight -> {
            if (flight.getAvailableSeats() <= 0) {
                flight.setAvailable(false);
            }
        });

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), flights.size());

        if (start > end) {
            return Page.empty(pageable);
        }

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

    @Override
    public List<FlightSimpleResponse> getAvailableFlightsByCity(String city) {
        List<Flight> flights = flightRepository.findAvailableFlightsByCity(city, LocalDateTime.now());
        return flights.stream()
                .map(flightMapper::toSimpleResponse)
                .toList();
    }
}

