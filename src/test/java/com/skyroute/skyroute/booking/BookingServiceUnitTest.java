package com.skyroute.skyroute.booking;

import com.skyroute.skyroute.airport.entity.Airport;
import com.skyroute.skyroute.booking.entity.Booking;
import com.skyroute.skyroute.booking.repository.BookingRepository;
import com.skyroute.skyroute.booking.service.BookingService;
import com.skyroute.skyroute.flight.entity.Flight;
import com.skyroute.skyroute.route.Route;
import com.skyroute.skyroute.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookingServiceUnitTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingService bookingService;

    private User testUser;
    private Flight testFlight;
    private Booking testBooking;
    private Airport testOrigin;
    private Airport testDestination;
    private Route testRoute;

    @BeforeEach
    void setUp() {


    }

}
