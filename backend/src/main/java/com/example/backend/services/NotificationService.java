package com.example.backend.services;

import com.example.backend.dtos.NotificationDTO;

public interface NotificationService {

    void sendEmail(NotificationDTO notificationDTO);

    void sendSMS();

    void sendWhatsApp();
}
