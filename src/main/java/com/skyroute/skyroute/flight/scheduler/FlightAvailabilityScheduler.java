package com.skyroute.skyroute.flight.scheduler;

import com.skyroute.skyroute.flight.service.FlightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class FlightAvailabilityScheduler {

    private final FlightService flightService;

    @Scheduled(cron = "${app.scheduler.flight-availability-cron}")
    public void updateFlightsAvailability() {
        LocalDateTime now = LocalDateTime.now();
        int updatedFlights = flightService.markFlightsAsUnavailableAndReleaseSeats(now);
        log.info("Scheduler executed at {} - updated flights: {}", now, updatedFlights);
    }
}
