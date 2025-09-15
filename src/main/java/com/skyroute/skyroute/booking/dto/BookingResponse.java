package com.skyroute.skyroute.booking.dto;

import com.skyroute.skyroute.booking.enums.BookingStatus;

import java.time.LocalDateTime;

public record BookingResponse(
        Long bookingId,
        String bookingNumber,
        BookingStatus bookingStatus,
        Long flightId,
        String flightNumber,
        String origin,
        String destination,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        String passengerNames,
        String passengerBirthDates,
        int seatsBooked,
        Double totalPrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
