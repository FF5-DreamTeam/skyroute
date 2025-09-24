package com.skyroute.skyroute.booking.service;

import com.skyroute.skyroute.booking.dto.BookingRequest;
import com.skyroute.skyroute.booking.dto.BookingResponse;
import com.skyroute.skyroute.booking.enums.BookingStatus;
import com.skyroute.skyroute.user.entity.User;
import org.springframework.data.domain.Page;

public interface BookingService {
    Page<BookingResponse> getAllBookingsAdmin(int page, int size, String sortBy, String sortDirection);
    Page<BookingResponse> getAllBookingsUser(User user, int page, int size, String sortBy, String sortDirection);
    BookingResponse getBookingById(Long id, User user);
    BookingResponse createBooking(BookingRequest request, User user);
    BookingResponse updateBookingStatus(Long id, BookingStatus status, User user);
    void cancelBooking(Long id, User user);
    BookingResponse confirmBooking(Long id, User user);
}
