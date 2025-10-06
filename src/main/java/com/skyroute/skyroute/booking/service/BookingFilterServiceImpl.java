package com.skyroute.skyroute.booking.service;

import com.skyroute.skyroute.booking.dto.BookingFilterRequest;
import com.skyroute.skyroute.booking.dto.BookingMapper;
import com.skyroute.skyroute.booking.dto.BookingResponse;
import com.skyroute.skyroute.booking.entity.Booking;
import com.skyroute.skyroute.booking.repository.BookingRepository;
import com.skyroute.skyroute.booking.specification.BookingSpecification;
import com.skyroute.skyroute.user.entity.User;
import com.skyroute.skyroute.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingFilterServiceImpl implements BookingFilterService{
    private final BookingRepository bookingRepository;

    @Override
    public Page<BookingResponse> filterBookings(BookingFilterRequest filterRequest, Pageable pageable, User user) {
        Specification<Booking> specification = buildSpecificationByRole(filterRequest, user);
        return bookingRepository.findAll(specification, pageable).map(booking -> BookingMapper.toDto(booking));
    }

    private Specification<Booking> buildSpecificationByRole(BookingFilterRequest filterRequest, User user) {
        return user.getRole() == Role.ADMIN
            ? buildAdminSpecification(filterRequest)
            : buildUserSpecification(filterRequest, user);
    }

    private Specification<Booking> buildUserSpecification(BookingFilterRequest filterRequest, User user) {
        Specification<Booking> specification = BookingSpecification.hasUserId(user.getId());
        return specification.and(buildCommonFilters(filterRequest));
    }

    private Specification<Booking> buildAdminSpecification(BookingFilterRequest filterRequest) {
        Specification<Booking> specification = Specification.unrestricted();

        if (filterRequest == null) {
            return specification;
        }

        specification = specification.and(buildAdminOnlyFilters(filterRequest));
        specification = specification.and(buildCommonFilters(filterRequest));
        return specification;
    }

    private Specification<Booking> buildAdminOnlyFilters(BookingFilterRequest filterRequest) {
        Specification<Booking> specification = Specification.unrestricted();
         specification = specification.and(buildUserFilters(filterRequest));
         specification = specification.and(buildFlightFilters(filterRequest));
         specification = specification.and(buildAdminChecks(filterRequest));
         return specification;
    }

    private Specification<Booking> buildUserFilters(BookingFilterRequest filterRequest) {
        Specification<Booking> specification = Specification.unrestricted();

        if (filterRequest.userId() != null) {
            specification = specification.and(BookingSpecification.hasUserId(filterRequest.userId()));
        }

        if (filterRequest.userEmail() != null && !filterRequest.userEmail().isEmpty()) {
            specification = specification.and(BookingSpecification.hasUserEmail(filterRequest.userEmail()));
        }

        if (filterRequest.userName() != null && !filterRequest.userName().isEmpty()) {
            specification = specification.and(BookingSpecification.hasUserName(filterRequest.userName()));
        }

        return specification;
    }

    private Specification<Booking> buildFlightFilters(BookingFilterRequest filterRequest) {
        Specification<Booking> specification = Specification.unrestricted();

        if (filterRequest.flightId() != null) {
            specification = specification.and(BookingSpecification.hasFlightId(filterRequest.flightId()));
        }

        if (filterRequest.flightNumber() != null && !filterRequest.flightNumber().isEmpty()) {
            specification = specification.and(BookingSpecification.hasFlightNumber(filterRequest.flightNumber()));
        }

        return specification;
    }

    private Specification<Booking> buildAdminChecks(BookingFilterRequest filterRequest) {
        Specification<Booking> specification = Specification.unrestricted();

        if (Boolean.TRUE.equals(filterRequest.activeOnly())) {
            specification = specification.and(BookingSpecification.isActive());
        }

        if (Boolean.TRUE.equals(filterRequest.pendingOnly())) {
            specification = specification.and(BookingSpecification.isPending());
        }

        return specification;
    }
    private Specification<Booking> buildCommonFilters(BookingFilterRequest filterRequest) {

        if (filterRequest == null) {
            return Specification.unrestricted();
        }

        Specification<Booking> specification = Specification.unrestricted();
        specification = specification.and(buildBookingStatusFilters(filterRequest));
        specification = specification.and(buildDateFilters(filterRequest));
        specification = specification.and(buildPriceFilters(filterRequest));
        specification = specification.and(buildAirportFilters(filterRequest));
        specification = specification.and(buildPassengerFilters(filterRequest));
        specification = specification.and(buildTimeFilters(filterRequest));
        return specification;
    }

    private Specification<Booking> buildBookingStatusFilters(BookingFilterRequest filterRequest) {
        Specification<Booking> specification = Specification.unrestricted();

        if (filterRequest.bookingStatus() != null) {
            specification = specification.and(BookingSpecification.hasStatus(filterRequest.bookingStatus()));
        }

        if (filterRequest.bookingNumber() != null && !filterRequest.bookingNumber().isEmpty()) {
            specification = specification.and(BookingSpecification.hasBookingNumber(filterRequest.bookingNumber()));
        }

        return specification;
    }

    private Specification<Booking> buildDateFilters(BookingFilterRequest filterRequest) {
        Specification<Booking> specification = Specification.unrestricted();

        if (filterRequest.flightDepartureDate() != null) {
            specification = specification.and(BookingSpecification.hasFlightDepartureDate(filterRequest.flightDepartureDate()));
        }

        return specification;
    }

    private Specification<Booking> buildPriceFilters(BookingFilterRequest filterRequest) {
        Specification<Booking> specification = Specification.unrestricted();

        if (filterRequest.minPrice() != null || filterRequest.maxPrice() != null) {
            specification = specification.and(BookingSpecification.hasPriceBetween(filterRequest.minPrice(), filterRequest.maxPrice()));
        }

        return specification;
    }

    private Specification<Booking> buildAirportFilters(BookingFilterRequest filterRequest) {
        Specification<Booking> specification = Specification.unrestricted();

        if (filterRequest.originAirport() != null && !filterRequest.originAirport().isEmpty()) {
            specification = specification.and(BookingSpecification.hasOriginAirportOrCode(filterRequest.originAirport()));
        }

        if (filterRequest.destinationAirport() != null && !filterRequest.destinationAirport().isEmpty()) {
            specification = specification.and(BookingSpecification.hasDestinationAirportOrCode(filterRequest.destinationAirport()));
        }

        return specification;
    }

    private Specification<Booking> buildPassengerFilters(BookingFilterRequest filterRequest) {
        Specification<Booking> specification = Specification.unrestricted();

        if (filterRequest.passengerName() != null && !filterRequest.passengerName().isEmpty()) {
            specification = specification.and(BookingSpecification.hasPassengerName(filterRequest.passengerName()));
        }

        return specification;
    }

    private Specification<Booking> buildTimeFilters(BookingFilterRequest filterRequest) {
        Specification<Booking> specification = Specification.unrestricted();

        if (Boolean.TRUE.equals(filterRequest.futureFlightsOnly())) {
            specification = specification.and(BookingSpecification.hasFutureFlights());
        } else if (Boolean.FALSE.equals(filterRequest.futureFlightsOnly())) {
            specification = specification.and(BookingSpecification.hasPastFlights());
        }

        return specification;
    }
}