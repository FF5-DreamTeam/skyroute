package com.skyroute.skyroute.airport.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "airports")
@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
public class Airport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    private String city;

    private String imageUrl;
}