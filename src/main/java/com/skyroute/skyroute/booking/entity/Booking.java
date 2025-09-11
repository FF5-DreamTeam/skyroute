package com.skyroute.skyroute.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name= "seats_booked")
    private int seatsBooked;

    @Column(name= "total_price", nullable = false)
    private Double totalPrice;

    /*@Column(name= "booking_status", nullable = false)
    private BookingStatus bookingStatus;*/

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;*/

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", referencedColumnName = "id", nullable = false)
    private Flight flight;*/
}
