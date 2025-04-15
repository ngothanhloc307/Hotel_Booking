package com.example.HotelBooking;

import com.example.HotelBooking.dtos.NotificationDTO;
import com.example.HotelBooking.enums.NotificationType;
import com.example.HotelBooking.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HotelBookingApplication implements CommandLineRunner {

    @Autowired
    private NotificationService notificationService;

    public static void main(String[] args) {
        SpringApplication.run(HotelBookingApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .type(NotificationType.EMAIL)
                .recipient("locitc90@gmail.com")
                .body("This is a test email form command line")
                .subject("Test Email Sending")
                .build();
        notificationService.sendEmail(notificationDTO);
    }
}
