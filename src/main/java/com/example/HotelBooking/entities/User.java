package com.example.HotelBooking.entities;

import com.example.HotelBooking.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @NotBlank(message = "Email is required")
    @Column(unique = true)
    private String email;
    private String password;

    @NotBlank(message = "Phone number is required")
    @Column(name = "phone_number")
    private String PhoneNumber;

    @Enumerated(EnumType.STRING)
    private UserRole role; // e.g CUSTOMER, ADMIN

    private boolean isActive;
    private final LocalDateTime createdAt = LocalDateTime.now();

}
