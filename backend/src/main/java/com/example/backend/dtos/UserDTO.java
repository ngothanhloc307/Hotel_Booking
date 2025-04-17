package com.example.backend.dtos;

import com.example.backend.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {

    private Long id;

    private String firstName;
    private String lastName;

    private String email;
    private String password;

    private String PhoneNumber;

    private UserRole role; // e.g CUSTOMER, ADMIN

    private boolean isActive;
    private final LocalDateTime createdAt = LocalDateTime.now();
}


