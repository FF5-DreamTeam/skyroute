package com.skyroute.skyroute.flight.entity;

import com.skyroute.skyroute.shared.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "flights")
@Entity
public class FlightEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String flightNumber;

    @Min(0)
    private int availableSeats;

    @Future
    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Future
    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Min(0)
    private double price;

    @Column(nullable = false)
    private boolean available;

//    @ManyToOne(fetch = FetchType.Lazy)
//    @JoinColumn(name = "aircraft_id", nullable = false)
//    private AircraftEntity aircraft;
//
//    @ManyToOne
//    @JoinColumn(name = "route_id", nullable = false)
//    private Route route;
//
//    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<BookingEntity> bookings;
//
//    @Version
//    private Long version;
}
