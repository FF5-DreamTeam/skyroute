package com.skyroute.skyroute.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "booking")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking /*extends Auditable*/ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "booking_number", nullable = false, length = 50)
    private String bookingNumber;

    @Column(name= "seat_booked")
    private int seatBooked;

    @Column(name= "passenger_name")
    private String passengerName;

    @Column(name= "passenger_booked")
    private String passengerPassport;

    @Column(name= "passenger_birth_date")
    private LocalDate passengerBirthDate;

    @Column(nullable = false)
    private Double price;

    /*@Enumerated(EnumType.STRING)
    @Column(name= "booking_status")
    private BookingStatus bookingStatus;*/

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;*/

   /* @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", referencedColumnName = "id", nullable = false)
    private Flight flight;*/
}
