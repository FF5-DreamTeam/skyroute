package com.skyroute.skyroute.booking.repository;

import com.skyroute.skyroute.booking.entity.Booking;
import com.skyroute.skyroute.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @EntityGraph(attributePaths = {"flight", "flight.route", "user"})
    Page<Booking> findAllByUser(Pageable pageable, User user);
}
