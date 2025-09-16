package com.skyroute.skyroute.aircraft.repository;

import com.skyroute.skyroute.aircraft.entity.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, Long> {
}