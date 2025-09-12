package com.skyroute.skyroute.booking.dto;

import com.skyroute.skyroute.booking.entity.Booking;

import java.time.format.DateTimeFormatter;

public class BookingMapper {
    public static Booking toEntity(BookingRequest request /*, User user*/) {
        return Booking.builder()
                /*.flightId(request.flightId())*/
                .passengerName(request.passengerName())
                .passengerPassport(request.passengerPassport())
                .passengerBirthDate(request.passengerBirthDate())
                .seatBooked(request.seatBooked())
                /*.user(user)*/
                .build();
    }

    public static BookingResponse toDto(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getBookingNumber(),
                booking.getBookingStatus(),
                booking.getFlight().getFlightId(),
                booking.getFlight().getFlightNumber(),
                booking.getFlight().getRoute().getOrigin(),
                booking.getFlight().getRoute().getDestination(),
                booking.getFlight().getDepartureTime(),
                booking.getFlight().getArrivalTime(),
                booking.getPassengerName(),
                booking.getPassengerPassport(),
                booking.getPassengerBirthDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                booking.getSeatBooked(),
                booking.getPrice(),
                booking.getCreatedAt()
        );
    }
}
