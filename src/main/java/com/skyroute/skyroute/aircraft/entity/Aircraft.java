package com.skyroute.skyroute.aircraft.entity;

import com.skyroute.skyroute.flight.entity.Flight;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "aircrafts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aircraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int capacity;

    private String model;

    private String manufacturer;

    @OneToMany(mappedBy = "aircraft", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Flight> flights;
}

