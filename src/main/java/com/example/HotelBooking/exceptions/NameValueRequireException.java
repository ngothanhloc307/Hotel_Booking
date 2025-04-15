package com.example.HotelBooking.exceptions;

public class NameValueRequireException extends RuntimeException {
    public NameValueRequireException(String message) {
        super(message);
    }
}
