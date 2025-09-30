package com.skyroute.skyroute.flight;

import com.skyroute.skyroute.flight.scheduler.FlightAvailabilityScheduler;
import com.skyroute.skyroute.flight.service.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class FlightAvailabilitySchedulerTest {

    @Mock
    private FlightService flightService;

    @InjectMocks
    private FlightAvailabilityScheduler scheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Scheduler should call FlightService with current time and update availability")
    void testSchedulerCallsFlightServiceWithNow() {
        when(flightService.markFlightsAsUnavailableAndReleaseSeats(any(LocalDateTime.class)))
                .thenReturn(5);
        scheduler.updateFlightsAvailability();
        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(flightService, times(1))
                .markFlightsAsUnavailableAndReleaseSeats(captor.capture());

        LocalDateTime capturedTime = captor.getValue();
        LocalDateTime now = LocalDateTime.now();
        assertThat(capturedTime)
                .isAfter(now.minusSeconds(2))
                .isBefore(now.plusSeconds(2));
    }

    @Test
    @DisplayName("Scheduler should handle case when no flights are updated")
    void testSchedulerNoFlightsUpdated() {
        when(flightService.markFlightsAsUnavailableAndReleaseSeats(any(LocalDateTime.class)))
                .thenReturn(0);
        scheduler.updateFlightsAvailability();
        verify(flightService, times(1))
                .markFlightsAsUnavailableAndReleaseSeats(any(LocalDateTime.class));
    }
}

