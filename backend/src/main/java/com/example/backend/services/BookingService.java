package com.example.backend.services;

import com.example.backend.dtos.BookingDTO;
import com.example.backend.dtos.Response;

public interface BookingService {

    Response getAllBookings();

    Response createBooking(BookingDTO bookingDTO);

    Response updateBooking(BookingDTO bookingDTO);

    Response findBookingByReferenceNo(String bookingReference);
}
