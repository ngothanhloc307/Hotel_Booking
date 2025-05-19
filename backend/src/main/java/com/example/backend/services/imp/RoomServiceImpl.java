package com.example.backend.services.imp;

import com.example.backend.dtos.Response;
import com.example.backend.dtos.RoomDTO;
import com.example.backend.entities.Room;
import com.example.backend.enums.RoomType;
import com.example.backend.exceptions.InvalidBookingStateAndDateException;
import com.example.backend.exceptions.NotFoundException;
import com.example.backend.repositories.RoomRepository;
import com.example.backend.services.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;

    private static final String IMAGE_DIRECTORY = System.getProperty("user.dir") + "/product-image/";


    @Override
    public Response addRoom(RoomDTO roomDTO, MultipartFile imageFile) {
        Room roomToSave = modelMapper.map(roomDTO, Room.class);
        Integer id = roomDTO.getRoomNumber();
        if(imageFile !=null){
            String imagePath = saveImage(imageFile);
            roomToSave.setImageUrl(imagePath);
        }
        roomRepository.save(roomToSave);

        return Response.builder()
                .status(200)
                .message("Successfully room " + id +" added ")
                .build();
    }

    @Override
    public Response updateRoom(RoomDTO roomDTO, MultipartFile imageFile) {
       Room existingRoom = roomRepository.findById(roomDTO.getId())
               .orElseThrow(()-> new NotFoundException("Room is not found"));
       if(imageFile !=null && !imageFile.isEmpty()){
           String imagePath = saveImage(imageFile);
           existingRoom.setImageUrl(imagePath);
       }

       if(roomDTO.getRoomNumber() !=null & roomDTO.getRoomNumber() >=0){
           existingRoom.setRoomNumber(roomDTO.getRoomNumber());
       }
       if(roomDTO.getPricePerNight() !=null && roomDTO.getPricePerNight().compareTo(BigDecimal.ZERO) >=0){
           existingRoom.setPricePerNight(roomDTO.getPricePerNight());
       }
       if(roomDTO.getCapacity() !=null && roomDTO.getCapacity() >0){
           existingRoom.setCapacity(roomDTO.getCapacity());
       }
       if(roomDTO.getRoomType() !=null)
           existingRoom.setRoomType(roomDTO.getRoomType());
       if(roomDTO.getDescription() !=null) existingRoom.setDescription(roomDTO.getDescription());

       roomRepository.save(existingRoom);

       return Response.builder()
               .status(200)
               .message("Updated room " + roomDTO.getRoomNumber() +" successfully")
               .build();
    }

    @Override
    public Response getAllRoom() {
       List<Room> roomList = roomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
       List<RoomDTO> roomDTOList = modelMapper.map(roomList, new TypeToken<List<RoomDTO>>() {}.getType());

       return Response.builder()
               .status(200)
               .message("Successfully retrieved rooms")
               .rooms(roomDTOList)
               .build();
    }

    @Override
    public Response getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Room is not found"));
        RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);

        return  Response.builder()
                .status(200)
                .message("success")
                .room(roomDTO)
                .build();
    }

    @Override
    public Response deleteRoom(Long id) {
        if(!roomRepository.existsById(id)){
            throw new NotFoundException("Room is not found");
        }

        roomRepository.deleteById(id);

        return Response.builder()
                .status(200)
                .message("Room Delete Successfully")
                .build();
    }

    @Override
    public Response getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, RoomType roomType) {

        //validation: Ensure the check-in date is not before to day
        if(checkInDate.isBefore(LocalDate.now())){
            throw new InvalidBookingStateAndDateException("Checkin date is can not before today");
        }
        //validation: Ensure the check-in date is not before to day
        if(checkOutDate.isBefore(checkInDate)){
            throw new InvalidBookingStateAndDateException("Check out date can not before Check in date");
        }
        //validation: Ensure the check-out date is not equal check-in date
        if(checkOutDate.isEqual(checkInDate)){
            throw new InvalidBookingStateAndDateException("Check out date is not equal Check in date");
        }

        List<Room> roomList = roomRepository.findAvailableRooms(checkInDate, checkOutDate, roomType);
        List<RoomDTO> roomDTOList = modelMapper.map(roomList, new TypeToken<List<RoomDTO>>() {}.getType());

        return Response.builder()
                .status(200)
                .rooms(roomDTOList)
                .build();
    }

    @Override
    public List<RoomType> getAllRoomTypes() {
       return Arrays.asList(RoomType.values());
    }

    @Override
    public Response searchRooms(String input) {
        List<Room> roomList = roomRepository.searchRooms(input);
        List<RoomDTO> roomDTOList = modelMapper.map(roomList, new TypeToken<List<RoomDTO>>() {}.getType());

        return Response.builder()
                .status(200)
                .message("Search Successfully")
                .rooms(roomDTOList)
                .build();
    }

    private String saveImage(MultipartFile imageFile) {
        if(!imageFile.getContentType().startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        // Create a directory to store image if it doesn't exist
        File directory = new File(IMAGE_DIRECTORY);

        if(!directory.exists()) {
            directory.mkdir();
        }

        //Generate unique file name for image
        String uniqueFileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        //get the absolute path of the image
        String imagePath = IMAGE_DIRECTORY + uniqueFileName;

        try{
            File destinationFile = new File(imagePath);
            imageFile.transferTo(destinationFile);
        }catch (Exception ex){
            throw new IllegalArgumentException(ex.getMessage());
        }
        return imagePath;
    }
}
