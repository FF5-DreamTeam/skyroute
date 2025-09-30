package com.skyroute.skyroute.booking.service;

import com.skyroute.skyroute.booking.dto.BookingFilterRequest;
import com.skyroute.skyroute.booking.dto.BookingMapper;
import com.skyroute.skyroute.booking.dto.BookingResponse;
import com.skyroute.skyroute.booking.entity.Booking;
import com.skyroute.skyroute.booking.repository.BookingRepository;
import com.skyroute.skyroute.booking.specification.BookingSpecification;
import com.skyroute.skyroute.user.entity.User;
import com.skyroute.skyroute.user.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BookingFilterServiceImpl implements BookingFilterService{
    private final BookingRepository bookingRepository;

    public BookingFilterServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Page<BookingResponse> filterBookings(BookingFilterRequest filterRequest, Pageable pageable, User user) {
        Specification<Booking> specification = buildSpecification(filterRequest, user);
        return bookingRepository.findAll(specification, pageable).map(booking -> BookingMapper.toDto(booking));
    }

    private Specification<Booking> buildSpecification(BookingFilterRequest filterRequest, User user) {
        Specification<Booking> specification = Specification.unrestricted();
        if (user.getRole() != Role.ADMIN) {
            specification = specification.and(BookingSpecification.hasUserId(user.getId()));
        }

        if (filterRequest == null) {
            return specification;
        }

        if (filterRequest.bookingStatus() != null) {
            specification = specification.and(BookingSpecification.hasStatus(filterRequest.bookingStatus()));
        }

        if (filterRequest.bookingNumber() != null && !filterRequest.bookingNumber().isEmpty()) {
            specification = specification.and(BookingSpecification.hasBookingNumber(filterRequest.bookingNumber()));
        }

        if (filterRequest.createdFrom() != null || filterRequest.createdTo() != null) {
            specification = specification.and(BookingSpecification.createdBetween(filterRequest.createdFrom(), filterRequest.createdTo()));
        }

        if (filterRequest.flightDepartureDate() != null) {
            specification = specification.and(BookingSpecification.hasFlightDepartureDate(filterRequest.flightDepartureDate()));
        }

        if (filterRequest.flightDepartureFrom() != null || filterRequest.flightDepartureTo() != null) {
            specification = specification.and(BookingSpecification.hasFlightDepartureBetween(filterRequest.flightDepartureFrom(), filterRequest.flightDepartureTo()));
        }

        if (filterRequest.minPrice() != null || filterRequest.maxPrice() != null) {
            specification = specification.and(BookingSpecification.hasPriceBetween(filterRequest.minPrice(), filterRequest.maxPrice()));
        }

        if (filterRequest.exactSeats() != null) {
            specification = specification.and(BookingSpecification.hasExactSeats(filterRequest.exactSeats()));
        }

        if (filterRequest.minSeats() != null) {
            specification = specification.and(BookingSpecification.hasMinimumSeats(filterRequest.minSeats()));
        }

        if (user.getRole() == Role.ADMIN) {
            if (filterRequest.userId() != null) {
                specification = specification.and(BookingSpecification.hasUserId(filterRequest.userId()));
            }

            if (filterRequest.userEmail() != null && !filterRequest.userEmail().isEmpty()) {
                specification = specification.and(BookingSpecification.hasUserEmail(filterRequest.userEmail()));
            }

            if (filterRequest.userName() != null && !filterRequest.userName().isEmpty()) {
                specification = specification.and(BookingSpecification.hasUserName(filterRequest.userName()));
            }
        }

        if (filterRequest.flightId() != null) {
            specification = specification.and(BookingSpecification.hasFlightId(filterRequest.flightId()));
        }

        if (filterRequest.flightNumber() != null && !filterRequest.flightNumber().isEmpty()) {
            specification = specification.and(BookingSpecification.hasFlightNumber(filterRequest.flightNumber()));
        }

        boolean originAirportPresent = filterRequest.originAirport() != null && !filterRequest.originAirport().isEmpty();
        if (originAirportPresent) {
            specification = specification.and(BookingSpecification.hasOriginAirport(filterRequest.originAirport()));
        }

        boolean departureAirportPresent = filterRequest.destinationAirport() != null && !filterRequest.destinationAirport().isEmpty();
        if (departureAirportPresent) {
            specification = specification.and(BookingSpecification.hasDestinationAirport(filterRequest.destinationAirport()));
        }

        if (originAirportPresent && departureAirportPresent) {
            specification = specification.and(BookingSpecification.hasRoute(filterRequest.originAirport(), filterRequest.destinationAirport()));
        }

        if (filterRequest.originAirportCode() != null && !filterRequest.originAirportCode().isEmpty()) {
            specification = specification.and(BookingSpecification.hasOriginAirportCode(filterRequest.originAirportCode()));
        }

        if (filterRequest.destinationAirportCode() != null && !filterRequest.destinationAirportCode().isEmpty()) {
            specification = specification.and(BookingSpecification.hasDestinationAirportCode(filterRequest.destinationAirportCode()));
        }

        if (filterRequest.passengerName() != null && !filterRequest.passengerName().isEmpty()) {
            specification = specification.and(BookingSpecification.hasPassengerName(filterRequest.passengerName()));
        }

        if (Boolean.TRUE.equals(filterRequest.futureFlightsOnly())) {
            specification = specification.and(BookingSpecification.hasFutureFlights());
        } else if (Boolean.FALSE.equals(filterRequest.futureFlightsOnly())) {
            specification = specification.and(BookingSpecification.hasPastFlights());
        }

        if (Boolean.TRUE.equals(filterRequest.activeOnly())) {
            specification = specification.and(BookingSpecification.isActive());
        }

        if (Boolean.TRUE.equals(filterRequest.cancelledOnly())) {
            specification = specification.and(BookingSpecification.isCancelled());
        }

        if (Boolean.TRUE.equals(filterRequest.confirmedOnly())) {
            specification = specification.and(BookingSpecification.isConfirmed());
        }

        if (Boolean.TRUE.equals(filterRequest.pendingOnly())) {
            specification = specification.and(BookingSpecification.isPending());
        }
        return specification;
    }
}
