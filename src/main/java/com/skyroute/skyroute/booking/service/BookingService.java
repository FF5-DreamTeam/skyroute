package com.skyroute.skyroute.booking.service;

import com.skyroute.skyroute.booking.dto.BookingResponse;
import com.skyroute.skyroute.user.entity.User;
import org.springframework.data.domain.Page;

public interface BookingService {
    public Page<BookingResponse> getAllBookingsAdmin(int page, int size, String sortBy, String sortDirection);

    public Page<BookingResponse> getAllBookingsUser(User user, int page, int size, String sortBy, String sortDirection);
}
