package com.skyroute.skyroute.booking.service;

import com.skyroute.skyroute.booking.dto.BookingFilterRequest;
import com.skyroute.skyroute.booking.dto.BookingResponse;
import com.skyroute.skyroute.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingFilterService {
    Page<BookingResponse> filterBookings(BookingFilterRequest filterRequest, Pageable pageable, User user);
}
