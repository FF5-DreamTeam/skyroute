package com.skyroute.skyroute.booking.service;

import com.skyroute.skyroute.booking.dto.BookingMapper;
import com.skyroute.skyroute.booking.dto.BookingRequest;
import com.skyroute.skyroute.booking.dto.BookingResponse;
import com.skyroute.skyroute.booking.entity.Booking;
import com.skyroute.skyroute.booking.enums.BookingStatus;
import com.skyroute.skyroute.email.EmailService;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.flight.service.FlightService;
import com.skyroute.skyroute.shared.exception.custom_exception.*;
import com.skyroute.skyroute.booking.repository.BookingRepository;
import com.skyroute.skyroute.user.entity.User;
import com.skyroute.skyroute.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final FlightService flightService;
    private final EmailService emailService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "bookingNumber", "bookingStatus", "createdAt",
            "flightNumber");

    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponse> getAllBookingsAdmin(int page, int size, String sortBy, String sortDirection) {
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        return bookingRepository.findAll(pageable).map(booking -> BookingMapper.toDto(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponse> getAllBookingsUser(User user, int page, int size, String sortBy, String sortDirection) {
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
        flightService.updateAvailabilityIfNeeded(request.flightId());
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

        validateUserStatusChangePermissions(user, previousStatus, newStatus, booking);

        booking.setBookingStatus(newStatus);

        handleSeatReleaseIfNeeded(newStatus, previousStatus, booking);

        Booking updatedBooking = bookingRepository.save(booking);

        sendStatusChangeNotifications(newStatus, previousStatus, updatedBooking);

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
        validateUserAccess(booking, user);
        if (user.getRole() == Role.USER && booking.getBookingStatus() != BookingStatus.CREATED) {
            throw new BookingAccessDeniedException("Cannot modify passenger names after booking is CONFORMED or CANCELLED");
        }

        booking.setPassengerNames(names);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponse updatePassengerBirthDates(Long id, List<LocalDate> birthDates, User user) {
        Booking booking = findBookingById(id);
        validateUserAccess(booking, user);
        if (user.getRole() == Role.USER && booking.getBookingStatus() != BookingStatus.CREATED) {
            throw new BookingAccessDeniedException("Cannot modify passenger birth dates after booking is CONFORMED or CANCELLED");
        }

        booking.setPassengerBirthDates(birthDates);
        return BookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public void deleteBooking(Long id, User user) {
        Booking booking = findBookingById(id);
        validateUserAccess(booking, user);

        if (user.getRole() == Role.USER && booking.getBookingStatus() != BookingStatus.CREATED) {
            throw new BookingAccessDeniedException("Users can only delete bookings in CREATED status");
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

        int maxSize = 10;
        size = Math.min(size, maxSize);

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            sortBy = "createdAt";
        }

        if (!"ASC".equalsIgnoreCase(sortDirection) && !"DESC".equalsIgnoreCase(sortDirection)) {
            sortDirection = "DESC";
        }

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        return PageRequest.of(page, size, sort);
    }

    private Booking findBookingById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Booking not found"));
    }

    private void validateUserAccess(Booking booking, User user) {
        if (user.getRole() == Role.USER && !booking.getUser().getId().equals(user.getId())) {
            throw new BookingAccessDeniedException("User cannot access this booking");
        }
    }

    private void validateUserStatusChangePermissions(User user, BookingStatus currentStatus, BookingStatus newStatus, Booking booking) {
        if (user.getRole() != Role.USER) {
            return;
        }

        validateUserCannotConfirm(newStatus);
        validateCancellationTimeLimit(currentStatus, newStatus, booking);
    }

    private void validateUserCannotConfirm(BookingStatus newStatus) {
        if (newStatus == BookingStatus.CONFIRMED) {
            throw new BookingAccessDeniedException("Users cannot confirm bookings");
        }
    }

    private void validateCancellationTimeLimit(BookingStatus currentStatus, BookingStatus newStatus, Booking booking) {
        boolean isCancellingConfirmedBooking = currentStatus == BookingStatus.CONFIRMED && newStatus == BookingStatus.CANCELLED;

        if (!isCancellingConfirmedBooking) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime departure = booking.getFlight().getDepartureTime();
        LocalDateTime cancellationDeadLine = departure.minusHours(24);

        if (now.isAfter(cancellationDeadLine)) {
            throw new InvalidBookingOperationException("You can only cancel the booking up to 24 hours before the flight departure. Please contact our customer service for further assistance");
        }
    }

    private void handleSeatReleaseIfNeeded(BookingStatus newStatus, BookingStatus previousStatus, Booking booking) {
        boolean shouldReleaseSeats = newStatus == BookingStatus.CANCELLED && previousStatus != BookingStatus.CANCELLED;

        if (shouldReleaseSeats) {
            flightService.releaseSeats(booking.getFlight().getId(), booking.getBookedSeats());
        }
    }

    private void sendStatusChangeNotifications(BookingStatus newStatus, BookingStatus previousStatus, Booking booking) {
        boolean isNewlyConfirmed = newStatus == BookingStatus.CONFIRMED && previousStatus != BookingStatus.CONFIRMED;
        boolean isNewlyCancelled = newStatus == BookingStatus.CANCELLED && previousStatus != BookingStatus.CANCELLED;

        if (isNewlyConfirmed) {
            emailService.sendBookingConfirmationStatusEmail(booking, booking.getUser(), booking.getFlight());
        }

        if (isNewlyCancelled) {
            emailService.sendBookingCancellationEmail(booking, booking.getUser(), booking.getFlight());
        }
    }

    private void validateFlightBookingEligibility(Long flightId, int requestedSeats) {
        if (!flightService.isFlightAvailable(flightId)) {
            throw new InvalidBookingOperationException("Flight not available for booking");
        }

        if (!flightService.hasAvailableSeats(flightId, requestedSeats)) {
            Flight flight = flightService.findById(flightId);
            throw new NotEnoughSeatsException("Not enough seats available. Requested: " + requestedSeats + ". Available: " + flight.getAvailableSeats());
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
            throw new InvalidBookingOperationException("Booking is already in " + target + " status");
        }

        switch (current) {
            case CREATED:
                if (target != BookingStatus.CONFIRMED && target != BookingStatus.CANCELLED) {
                    throw new InvalidBookingOperationException("A CREATED booking can only be CONFIRMED or CANCELLED");
                }
                break;
            case CONFIRMED:
                if (target != BookingStatus.CANCELLED) {
                    throw new InvalidBookingOperationException("A CONFIRMED booking can only be CANCELLED");
                }
                break;
            case CANCELLED:
                throw new InvalidBookingOperationException("Cannot change status of a CANCELLED booking");
        }
    }
}
