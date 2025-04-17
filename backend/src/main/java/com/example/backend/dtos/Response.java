package com.example.backend.dtos;

import com.example.backend.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    //generic
    private int status;
    private String message;

    //for login
    private String token;
    private UserRole role;
    private Boolean isActive;
    private String expirationTime;

    //user data output
    private UserDTO user;
    private List<UserDTO> users;

    //Booking data output
    private BookingDTO booking;
    private List<BookingDTO> bookings;

    //  data output
    private RoomDTO room;
    private List<RoomDTO> rooms;

    //Payment data out
    private PaymentDTO payment;
    private List<PaymentDTO> payments;

    //Notification data out
    private NotificationDTO notification;
    private List<NotificationDTO> notifications;

    private final LocalDateTime timestamp = LocalDateTime.now();
}
