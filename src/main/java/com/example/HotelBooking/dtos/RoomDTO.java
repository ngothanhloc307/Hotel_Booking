package com.example.HotelBooking.dtos;

import com.example.HotelBooking.enums.RoomType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomDTO {

    private Long id;
    private Integer roomNumber;

    private RoomType roomType;

    private BigDecimal pricePerNight;

    private Integer capacity;

    private String description; // additional data fot the room
    private String imageUrl; // this will hold the room picture
}
