package com.skyroute.skyroute.booking.dto;

import com.skyroute.skyroute.booking.enums.BookingStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record BookingFilterRequest(
        BookingStatus bookingStatus,
        String bookingNumber,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate flightDepartureDate,
        Double minPrice,
        Double maxPrice,
        Long userId,
        String userEmail,
        String userName,
        Long flightId,
        String flightNumber,
        String originAirport,
        String destinationAirport,
        String passengerName,
        Boolean futureFlightsOnly,
        Boolean activeOnly,
        Boolean pendingOnly
) {
    public boolean hasAnyFilter() {
        return bookingStatus != null || bookingNumber != null || flightDepartureDate != null || minPrice != null || maxPrice != null || userId != null || userEmail != null || userName != null || flightId != null || flightNumber != null || originAirport != null || destinationAirport != null || passengerName != null || futureFlightsOnly != null || activeOnly != null || pendingOnly != null;
    }
}
