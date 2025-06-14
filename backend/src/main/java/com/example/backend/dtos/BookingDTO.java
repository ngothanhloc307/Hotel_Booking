package com.example.backend.dtos;


import com.example.backend.enums.BookingStatus;
import com.example.backend.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingDTO {

    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private PaymentStatus paymentStatus;
    private BigDecimal totalPrice;
    private String bookingReference;
    private LocalDateTime createdAt;
    private BookingStatus bookingStatus;
    private RoomDTO room;
    private Long roomId;
    private UserDTO user;
}
