package com.skyroute.skyroute.booking.dto;

import com.skyroute.skyroute.booking.entity.Booking;
import com.skyroute.skyroute.booking.enums.BookingStatus;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.user.entity.User;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class BookingMapper {
   private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

   public static Booking toEntity(BookingRequest request, User user, Flight flight, Double totalPrice) {
        return Booking.builder()
                .bookingNumber(generateBookingNumber())
                .bookedSeats(request.bookedSeats())
                .passengerNames(request.passengerNames())
                .passengerBirthDates(request.passengerBirthDates())
                .totalPrice(totalPrice)
                .bookingStatus(BookingStatus.CREATED)
                .user(user)
                .flight(flight)
                .build();
    }

    public static BookingResponse toDto(Booking booking) {
        List<String> formatBirthDates = booking.getPassengerBirthDates()
                .stream()
                .map(date -> date.format(DATE_TIME_FORMATTER))
                .toList();

        return new BookingResponse(
                booking.getId(),
                booking.getBookingNumber(),
                booking.getBookingStatus(),
                booking.getFlight().getId(),
                booking.getFlight().getFlightNumber(),
                booking.getFlight().getRoute().getOrigin().getCity(),
                booking.getFlight().getRoute().getDestination().getCity(),
                booking.getFlight().getDepartureTime(),
                booking.getFlight().getArrivalTime(),
                booking.getPassengerNames(),
                formatBirthDates,
                booking.getBookedSeats(),
                booking.getTotalPrice(),
                booking.getCreatedAt(),
                booking.getUpdatedAt()
        );
    }

    private static String generateBookingNumber() {
        return "SR-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
