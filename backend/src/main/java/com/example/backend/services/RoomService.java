package com.example.backend.services;

import com.example.backend.dtos.Response;
import com.example.backend.dtos.RoomDTO;
import com.example.backend.enums.RoomType;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {

    Response addRoom(RoomDTO roomDTO, MultipartFile imageFile);

    Response updateRoom(RoomDTO roomDTO, MultipartFile imageFile);

    Response getAllRoom();

    Response getRoomById(Long id);

    Response deleteRoom(Long id);

    Response getAvailableRooms(LocalDate checkinDate, LocalDate checkoutDate, RoomType roomType);

    List<RoomType> getAllRoomTypes();

    Response searchRooms(String input);
}
