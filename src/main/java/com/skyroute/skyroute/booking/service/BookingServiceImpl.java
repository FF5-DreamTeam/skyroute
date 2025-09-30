package com.skyroute.skyroute.booking.service;

import com.skyroute.skyroute.booking.dto.BookingMapper;
import com.skyroute.skyroute.booking.dto.BookingRequest;
import com.skyroute.skyroute.booking.dto.BookingResponse;
import com.skyroute.skyroute.booking.entity.Booking;
import com.skyroute.skyroute.booking.enums.BookingStatus;
import com.skyroute.skyroute.email.EmailService;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.flight.service.FlightService;
import com.skyroute.skyroute.shared.exception.custom_exception.AccessDeniedException;
import com.skyroute.skyroute.shared.exception.custom_exception.BusinessException;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import com.skyroute.skyroute.booking.repository.BookingRepository;
import com.skyroute.skyroute.user.entity.User;
import com.skyroute.skyroute.user.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final FlightService flightService;
    private final EmailService emailService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "bookingNumber", "bookingStatus", "createdAt",
            "flightNumber");

    public BookingServiceImpl(BookingRepository bookingRepository, FlightService flightService,
            EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.flightService = flightService;
        this.emailService = emailService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponse> getAllBookingsAdmin(int page, int size, String sortBy, String sortDirection) {
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        return bookingRepository.findAll(pageable).map(booking -> BookingMapper.toDto(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponse> getAllBookingsUser(User user, int page, int size, String sortBy,
            String sortDirection) {
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        return bookingRepository.findAllByUser(pageable, user).map(booking -> BookingMapper.toDto(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id, User user) {
        Booking booking = findBookingById(id);
        validateUserAccess(booking, user);
        return BookingMapper.toDto(booking);
    }

    @Override
    public BookingResponse createBooking(BookingRequest request, User user) {
        Flight flight = flightService.findById(request.flightId());
        validateFlightBookingEligibility(request.flightId(), request.bookedSeats());
        Double totalPrice = calculateTotalPrice(flight, request.bookedSeats());
        Booking booking = BookingMapper.toEntity(request, user, flight, totalPrice);
        flightService.bookSeats(request.flightId(), request.bookedSeats());
        Booking savedBooking = bookingRepository.save(booking);

        emailService.sendBookingConfirmationEmail(savedBooking, user, flight);

        return BookingMapper.toDto(savedBooking);
    }

    @Override
    public BookingResponse updateBookingStatus(Long id, BookingStatus newStatus, User user) {
        Booking booking = findBookingById(id);
        validateUserAccess(booking, user);
        validateStatusTransition(booking.getBookingStatus(), newStatus);

        BookingStatus previousStatus = booking.getBookingStatus();
        booking.setBookingStatus(newStatus);

        if (user.getRole() == Role.USER) {
            if (newStatus == BookingStatus.CONFIRMED) {
                throw new AccessDeniedException("Users cannot confirm booking");
            }
        }

        if (newStatus == BookingStatus.CANCELLED && previousStatus != BookingStatus.CANCELLED) {
            flightService.releaseSeats(booking.getFlight().getId(), booking.getBookedSeats());
        }

        Booking updatedBooking = bookingRepository.save(booking);
        return BookingMapper.toDto(updatedBooking);
    }

    @Override
    public void cancelBooking(Long id, User user) {
        updateBookingStatus(id, BookingStatus.CANCELLED, user);
    }

    @Override
    public BookingResponse confirmBooking(Long id, User user) {
        return updateBookingStatus(id, BookingStatus.CONFIRMED, user);
    }

    @Override
    public BookingResponse updatePassengerNames(Long id, List<String> names, User user) {
        Booking booking = findBookingById(id);
        if (user.getRole() == Role.USER && booking.getBookingStatus() != BookingStatus.CREATED) {
            throw new AccessDeniedException("Cannot modify passenger names after booking is CONFORMED or CANCELLED");
        }

        booking.setPassengerNames(names);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse updatePassengerBirthDates(Long id, List<LocalDate> birthDates, User user) {
        Booking booking = findBookingById(id);
        if (user.getRole() == Role.USER && booking.getBookingStatus() != BookingStatus.CREATED) {
            throw new AccessDeniedException(
                    "Cannot modify passenger birth dates after booking is CONFORMED or CANCELLED");
        }

        booking.setPassengerBirthDates(birthDates);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public void deleteBooking(Long id, User user) {
        Booking booking = findBookingById(id);
        validateUserAccess(booking, user);

        if (user.getRole() == Role.USER && booking.getBookingStatus() != BookingStatus.CREATED) {
            throw new AccessDeniedException("Users can only delete bookings in CREATED status");
        }

        if (booking.getBookingStatus() != BookingStatus.CANCELLED) {
            flightService.releaseSeats(booking.getFlight().getId(), booking.getBookedSeats());
        }

        bookingRepository.delete(booking);
    }

    private Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
        if (page < 0)
            throw new IllegalArgumentException("Page index must be 0 or greater");
        if (size <= 0)
            throw new IllegalArgumentException("Page size must be greater than 0");

        int maxSize = 100;
        size = Math.min(size, maxSize);

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            sortBy = "createdAt";
        }

        if (!"ASC".equalsIgnoreCase(sortDirection) && !"DESC".equalsIgnoreCase(sortDirection)) {
            sortDirection = "ASC";
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        return PageRequest.of(page, size, sort);
    }

    private Booking findBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Booking not found"));
    }

    private void validateUserAccess(Booking booking, User user) {
        if (user.getRole() == Role.USER && !booking.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("User cannot access this booking");
        }
    }

    private void validateFlightBookingEligibility(Long flightId, int requestedSeats) {
        if (!flightService.isFlightAvailable(flightId)) {
            throw new BusinessException("Flight not available for booking");
        }

        if (!flightService.hasAvailableSeats(flightId, requestedSeats)) {
            Flight flight = flightService.findById(flightId);
            throw new BusinessException("Not enough seats available. Requested: " + requestedSeats + ". Available: "
                    + flight.getAvailableSeats());
        }
    }

    private Double calculateTotalPrice(Flight flight, int bookedSeats) {
        if (bookedSeats <= 0) {
            throw new IllegalArgumentException("Number of seats booked mut be positive");
        }

        return flight.getPrice() * bookedSeats;
    }

    private void validateStatusTransition(BookingStatus current, BookingStatus target) {
        if (current == target) {
            throw new BusinessException("Booking is already in " + target + " status");
        }

        switch (current) {
            case CREATED:
                if (target != BookingStatus.CONFIRMED && target != BookingStatus.CANCELLED) {
                    throw new BusinessException("A CREATED booking can only be CONFIRMED or CANCELLED");
                }
                break;
            case CONFIRMED:
                if (target != BookingStatus.CANCELLED) {
                    throw new BusinessException("A CONFIRMED booking can only be CANCELLED");
                }
                break;
            case CANCELLED:
                throw new BusinessException("Cannot change status of a CANCELLED booking");
        }
    }
}
