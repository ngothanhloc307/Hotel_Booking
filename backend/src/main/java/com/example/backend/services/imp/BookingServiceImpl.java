package com.example.backend.services.imp;

import com.example.backend.dtos.BookingDTO;
import com.example.backend.dtos.NotificationDTO;
import com.example.backend.dtos.Response;
import com.example.backend.entities.Booking;
import com.example.backend.entities.Room;
import com.example.backend.entities.User;
import com.example.backend.enums.BookingStatus;
import com.example.backend.enums.PaymentStatus;
import com.example.backend.exceptions.InvalidBookingStateAndDateException;
import com.example.backend.exceptions.NotFoundException;
import com.example.backend.repositories.BookingRepository;
import com.example.backend.repositories.RoomRepository;
import com.example.backend.services.BookingCodeGenerator;
import com.example.backend.services.BookingService;
import com.example.backend.services.NotificationService;
import com.example.backend.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {


    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final BookingCodeGenerator bookingCodeGenerator;


    @Override
    public Response getAllBookings() {
       List<Booking> bookingList = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
       List<BookingDTO> bookingDTOList = modelMapper.map(bookingList, new TypeToken<List<BookingDTO>>() {}.getType());

       for(BookingDTO bookingDTO : bookingDTOList){
           bookingDTO.setUser(null);
           bookingDTO.setRoom(null);
       }

       return Response.builder()
               .status(200)
               .message("success")
               .bookings(bookingDTOList)
               .build();

    }

    @Override
    public Response createBooking(BookingDTO bookingDTO) {
        User currentUser = userService.getCurrentLoggedInUser();
        Room room = roomRepository.findById(bookingDTO.getRoomId()).orElseThrow(()->new NotFoundException("Room not found"));
        //validation: Ensure the check-in date is not before to day
        if(bookingDTO.getCheckInDate().isBefore(LocalDate.now())){
            throw new InvalidBookingStateAndDateException("Checkin date is can not before today");
        }
        //validation: Ensure the check-in date is not before to day
        if (bookingDTO.getCheckOutDate().isBefore(bookingDTO.getCheckInDate())) {
            throw new InvalidBookingStateAndDateException("Check out date can not be before Check in date");
        }
        //validation: Ensure the check-out date is not equal check-in date
        if(bookingDTO.getCheckInDate().isEqual(bookingDTO.getCheckOutDate())){
            throw new InvalidBookingStateAndDateException("Check in date is not equal Check out date");
        }

        // validate room availability
        boolean isAvailable = bookingRepository.isRoomAvailable(room.getId(), bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());
        if(!isAvailable){
            throw new InvalidBookingStateAndDateException("Room is not available for the selected date rangers");
        }
        //calculate the total price needed to pay for the stay
        BigDecimal totalPrice = calculateTotalPrice(room,bookingDTO);
        String bookingReference = bookingCodeGenerator.generateBookingReference();

        //create and save the booking
        Booking booking = new Booking();
        booking.setUser(currentUser);
        booking.setRoom(room);
        booking.setCheckInDate(bookingDTO.getCheckInDate());
        booking.setCheckOutDate(bookingDTO.getCheckOutDate());
        booking.setTotalPrice(totalPrice);
        booking.setBookingReference(bookingReference);
        booking.setBookingStatus(BookingStatus.BOOKED);
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());

        bookingRepository.save(booking); //save to database

        //generate the payment url which will be sent via mail
        String paymentUrl = "http://localhost:8080/api/v1/payments/" + bookingReference + "/" + totalPrice;

        log.info("Payment URL: {}", paymentUrl);

        //send notification via email
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(currentUser.getEmail())
                .subject("Booking Confirmation")
                .body(String.format("Your booking has been created successfully. Please proceed with your payment using the payment link below " +
                        "\n%s", paymentUrl))
                .bookingReference(bookingReference)
                .build();

        notificationService.sendEmail(notificationDTO);// sending email

        return Response.builder()
                .status(200)
                .message("Booking is successfully")
                .booking(bookingDTO)
                .build();

    }

    @Override
    public Response updateBooking(BookingDTO bookingDTO) {
        if(bookingDTO.getId() == null) throw new NotFoundException("Booking id is required");

        Booking existingBooking = bookingRepository.findById(bookingDTO.getId()).orElseThrow(()->new RuntimeException("Booking not found"));

        if(bookingDTO.getBookingStatus() != null){
            existingBooking.setBookingStatus(bookingDTO.getBookingStatus());
        }

        if(bookingDTO.getPaymentStatus() != null){
            existingBooking.setPaymentStatus(bookingDTO.getPaymentStatus());
        }
        bookingRepository.save(existingBooking);

        return Response.builder()
                .status(200)
                .message("Booking is successfully updated")
                .build();
    }

    @Override
    public Response findBookingByReferenceNo(String bookingReference) {
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(()-> new NotFoundException("Booking with reference No " + bookingReference + " not found"));
        BookingDTO bookingDTO = modelMapper.map(booking, BookingDTO.class);
        return Response.builder()
                .status(200)
                .message("Success")
                .booking(bookingDTO)
                .build();
    }

    private BigDecimal calculateTotalPrice(Room room, BookingDTO bookingDTO) {
        BigDecimal pricePerNight = room.getPricePerNight();
        long days = ChronoUnit.DAYS.between(bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate());
        return pricePerNight.multiply(BigDecimal.valueOf(days));
    }
}
