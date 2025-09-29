package com.skyroute.skyroute.booking.dto;

import com.skyroute.skyroute.booking.enums.BookingStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record BookingFilterRequest(
        BookingStatus bookingStatus,
        String bookingNumber,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime createdFrom,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime createdTo,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime flightDepartureDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime flightDepartureFrom,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime flightDepartureTo,
        Double minPrice,
        Double maxPrice,
        Integer exactSeats,
        Integer minSeats,
        Long userId,
        String userEmail,
        String userName,
        Long flightId,
        String flightNumber,
        String originAirport,
        String destinationAirport,
        String originAirportCode,
        String destinationAirportCode,
        String passengerName,
        Boolean futureFlightsOnly,
        Boolean activeOnly,
        Boolean cancelledOnly,
        Boolean confirmedOnly,
        Boolean pendingOnly
) {
    public boolean hasAnyFilter() {
        return bookingStatus != null || bookingNumber != null || createdFrom != null || createdTo != null || flightDepartureDate != null || flightDepartureFrom != null || flightDepartureTo != null || minPrice != null || maxPrice != null || exactSeats != null || minSeats != null || userId != null || userEmail != null || userName != null || flightId != null || flightNumber != null || originAirport != null || destinationAirport != null || originAirportCode != null || destinationAirportCode != null || passengerName != null || futureFlightsOnly != null || activeOnly != null || cancelledOnly != null || confirmedOnly != null || pendingOnly != null;
    }
}
