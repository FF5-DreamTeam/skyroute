package com.skyroute.skyroute.booking.service;

import com.skyroute.skyroute.booking.dto.BookingMapper;
import com.skyroute.skyroute.booking.dto.BookingResponse;
import com.skyroute.skyroute.booking.entity.Booking;
import com.skyroute.skyroute.shared.exception.custom_exception.AccessDeniedException;
import com.skyroute.skyroute.shared.exception.custom_exception.EntityNotFoundException;
import com.skyroute.skyroute.booking.repository.BookingRepository;
import com.skyroute.skyroute.user.entity.User;
import com.skyroute.skyroute.user.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class BookingServiceImpl implements BookingService{
    private final BookingRepository bookingRepository;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "bookingNumber", "bookingStatus", "createdAt", "flightNumber");

    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Page<BookingResponse> getAllBookingsAdmin(int page, int size, String sortBy, String sortDirection) {
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        return bookingRepository.findAll(pageable).map(booking -> BookingMapper.toDto(booking));
    }

    @Override
    public Page<BookingResponse> getAllBookingsUser(User user, int page, int size, String sortBy, String sortDirection) {
        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
        return bookingRepository.findAllByUser(pageable, user).map(booking -> BookingMapper.toDto(booking));
    }

    @Override
    public BookingResponse getBookingById(Long id, User user) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if (user.getRole().equals(Role.USER) && !booking.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("User cannot access this booking");
        }

        return BookingMapper.toDto(booking);
    }

    private Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
        if (page < 0) throw new IllegalArgumentException("Page index must be 0 or greater");
        if (size <= 0) throw new IllegalArgumentException("Page size must be greater than 0");

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
}
