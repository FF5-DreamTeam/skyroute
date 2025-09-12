package com.skyroute.skyroute.booking.dto;

import com.skyroute.skyroute.booking.entity.Booking;

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
}
