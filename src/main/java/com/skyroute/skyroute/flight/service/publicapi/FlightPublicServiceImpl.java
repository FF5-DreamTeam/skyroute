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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightPublicServiceImpl implements FlightPublicService {

    private final FlightRepository flightRepository;
    private final FlightMapper flightMapper;


    @Override
    public Page<FlightSimpleResponse> searchFlights(FlightSearchRequest request, Pageable pageable) {
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

        flights.forEach(flight -> {
            if (flight.getAvailableSeats() <= 0) {
                flight.setAvailable(false);
            }
        });

        List<FlightSimpleResponse> results = flights.stream()
                .map(flightMapper::toSimpleResponse)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), results.size());
        return new PageImpl<>(results.subList(start, end), pageable, results.size());
    }


    @Override
    public FlightSimpleResponse getFlightById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight with id: not found " + id));
        return toSimpleResponse(flight);
    }

    public Flight findById(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight with id: not found " + id));
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
                null,
                null,
                null
        );
    }
}
