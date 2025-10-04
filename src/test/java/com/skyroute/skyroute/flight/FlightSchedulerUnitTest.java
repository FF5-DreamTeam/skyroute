package com.skyroute.skyroute.flight;

import com.skyroute.skyroute.flight.scheduler.FlightAvailabilityScheduler;
import com.skyroute.skyroute.flight.service.FlightService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightAvailabilitySchedulerUnitTest {

    @Mock
    private FlightService flightService;

    @InjectMocks
    private FlightAvailabilityScheduler scheduler;

    @Test
    void updateFlightsAvailability_happyPath_serviceIsCalledOnce() {

        doReturn(10).when(flightService)
                .markFlightsAsUnavailableAndReleaseSeats(any(LocalDateTime.class));

        scheduler.updateFlightsAvailability();

        verify(flightService, times(1))
                .markFlightsAsUnavailableAndReleaseSeats(any(LocalDateTime.class));
    }

    @Test
    void updateFlightsAvailability_sadPath_handlesExceptionGracefully() {

        doThrow(new RuntimeException("DB Connection failed"))
                .when(flightService)
                .markFlightsAsUnavailableAndReleaseSeats(any(LocalDateTime.class));

        scheduler.updateFlightsAvailability();

        verify(flightService, times(1))
                .markFlightsAsUnavailableAndReleaseSeats(any(LocalDateTime.class));
    }
}