package com.skyroute.skyroute.booking.entity;

import com.skyroute.skyroute.booking.enums.BookingStatus;
import com.skyroute.skyroute.shared.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "booking")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "booking_number", nullable = false, length = 50)
    private String bookingNumber;

    @Column(name= "seats_booked")
    private int seatsBooked;

    @Column(name= "passenger_names")
    private List<String> passengerNames;

    @Column(name= "passenger_passports")
    private List<String> passengerPassports;

    @Column(name= "passenger_birth_dates")
    private List<LocalDate> passengerBirthDates;

    @Column(nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(name= "booking_status")
    private BookingStatus bookingStatus;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;*/

   /* @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", referencedColumnName = "id", nullable = false)
    private Flight flight;*/
}
