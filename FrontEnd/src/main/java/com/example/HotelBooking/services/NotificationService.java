package com.example.HotelBooking.services;

import com.example.HotelBooking.dtos.NotificationDTO;

public interface NotificationService {

    void sendEmail(NotificationDTO notificationDTO);

    void sendSMS();

    void sendWhatsApp();
}
