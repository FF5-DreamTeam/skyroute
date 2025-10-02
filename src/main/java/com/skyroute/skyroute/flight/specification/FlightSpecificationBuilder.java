package com.skyroute.skyroute.flight.specification;

import com.skyroute.skyroute.flight.entity.Flight;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;
import java.util.Optional;

@NoArgsConstructor
public class FlightSpecificationBuilder {
    private Specification<Flight> specification = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

    public static FlightSpecificationBuilder builder(){
        return new FlightSpecificationBuilder();
    }

    public FlightSpecificationBuilder originEquals(Optional<String> origin){
        origin.ifPresent(o -> specification = specification.and(FlightSpecification.hasOriginEquals(o)));
        return this;
    }

    public FlightSpecificationBuilder destinationEquals(Optional<String> destination){
        destination.ifPresent(d -> specification = specification.and(FlightSpecification.hasDestinationEquals(d)));
        return this;
    }

    public FlightSpecificationBuilder departureDateEquals(Optional<String> departureDate){
        departureDate.ifPresent(d -> specification = specification.and(FlightSpecification.hasDepartureDateEquals(d)));
        return this;
    }

    public FlightSpecificationBuilder passengersAvailable(Optional<Integer> passengers){
        passengers.ifPresent(p -> specification = specification.and(FlightSpecification.hasPassengersAvailable(p)));
        return this;
    }

    public FlightSpecificationBuilder onlyAvailable(Optional<LocalDateTime> now){
        now.ifPresent(n -> specification = specification.and(FlightSpecification.isOnlyAvailable(n)));
        return this;
    }

    public FlightSpecificationBuilder pricelessThanOrEqual(Optional<Double> maxPrice){
        maxPrice.ifPresent(m -> specification = specification.and(FlightSpecification.hasPricelessThanOrEqual(m)));
        return this;
    }

    public Specification<Flight> build(){
        return this.specification;
    }
}