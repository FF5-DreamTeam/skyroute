package com.skyroute.skyroute.booking.dto;

import java.time.LocalDateTime;

public record BookingResponse(
        Long bookingId,
        String bookingNumber,
        /*BookingStatus bookingStatus,*/
        Long flightId,
        String flightNumber,
        String origin,
        String destination,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        String passengerName,
        String passengerPassport,
        String passengerBirthDate,
        int seatBooked,
        Double price,
        LocalDateTime createdAt
) {
}
