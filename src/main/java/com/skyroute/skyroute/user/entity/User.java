package com.skyroute.skyroute.user.entity;

import com.skyroute.skyroute.booking.entity.Booking;
import com.skyroute.skyroute.shared.BaseEntity;
import com.skyroute.skyroute.user.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull
    @Past
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "user_img_url")
    private String userImgUrl;

    @NotBlank
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Size(min = 6)
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;
}
