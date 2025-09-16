package com.skyroute.skyroute.booking.dto;

import com.skyroute.skyroute.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        Long bookingId,
        String bookingNumber,
        BookingStatus bookingStatus,
        Long flightId,
        String flightNumber,
        String originAirport,
        String destinationAirport,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        List<String> passengerNames,
        List<String> passengerBirthDates,
        int seatsBooked,
        Double totalPrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
