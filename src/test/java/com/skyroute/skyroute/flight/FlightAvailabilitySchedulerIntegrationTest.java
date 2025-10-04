package com.skyroute.skyroute.flight;

import com.skyroute.skyroute.flight.scheduler.FlightAvailabilityScheduler;
import com.skyroute.skyroute.flight.service.FlightService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ActiveProfiles("test")
@SpringBootTest
@TestPropertySource(properties = {
        "spring.task.scheduling.enabled=false",
        "app.scheduler.enabled=true"
})
public class FlightAvailabilitySchedulerIntegrationTest {

    @SpyBean
    private FlightService flightService;

    @Autowired
    private FlightAvailabilityScheduler scheduler;

    private void verifyServiceInvocation(int times) {
        verify(flightService, times(times))
                .markFlightsAsUnavailableAndReleaseSeats(any(LocalDateTime.class));
    }

    @AfterEach
    void resetMocks() {
        reset(flightService);
    }

    @Test
    void testSchedulerExecutesAndMarksZeroFlights() {
        doReturn(0).when(flightService)
                .markFlightsAsUnavailableAndReleaseSeats(any(LocalDateTime.class));

        scheduler.updateFlightsAvailability();

        verifyServiceInvocation(1);
    }

    @Test
    void testSchedulerExecutesAndMarksMultipleFlights() {
        final int flightsUpdated = 5;
        doReturn(flightsUpdated).when(flightService)
                .markFlightsAsUnavailableAndReleaseSeats(any(LocalDateTime.class));

        scheduler.updateFlightsAvailability();

        verifyServiceInvocation(1);
    }

    @Test
    void testSchedulerHandlesCheckedExceptionGracefully() {
        doThrow(new RuntimeException("Simulated DB Connection Issue"))
                .when(flightService)
                .markFlightsAsUnavailableAndReleaseSeats(any(LocalDateTime.class));

        assertDoesNotThrow(() -> scheduler.updateFlightsAvailability());

        verifyServiceInvocation(1);
    }

    @Test
    void testSchedulerHandlesRuntimeExceptionGracefully() {
        doThrow(new RuntimeException("Simulated Null Pointer in Service"))
                .when(flightService)
                .markFlightsAsUnavailableAndReleaseSeats(any(LocalDateTime.class));

        assertDoesNotThrow(() -> scheduler.updateFlightsAvailability());

        verifyServiceInvocation(1);
    }
}

