package com.example.backend.payments.stripe;

import com.example.backend.dtos.NotificationDTO;
import com.example.backend.entities.Booking;
import com.example.backend.entities.Payments;
import com.example.backend.enums.NotificationType;
import com.example.backend.enums.PaymentGateway;
import com.example.backend.enums.PaymentStatus;
import com.example.backend.exceptions.NotFoundException;
import com.example.backend.payments.stripe.dto.PaymentRequest;
import com.example.backend.repositories.BookingRepository;
import com.example.backend.repositories.PaymentRepository;
import com.example.backend.services.NotificationService;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private NotificationService notificationService;

    @Value("${stripe.api.public.key}")
    private String secretKey;

    public String createPaymentIntent(PaymentRequest paymentRequest) {
        log.info("Inside createPaymentIntent method");
        Stripe.apiKey = secretKey;
        String bookingReference = paymentRequest.getBookingReference();

        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(()-> new NotFoundException("Booking Reference Not Found"));
        if(booking.getPaymentStatus() == PaymentStatus.COMPLETED){
            throw new NotFoundException("Payment already made for this booking");
        }

        try{
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(paymentRequest.getAmount().multiply(BigDecimal.valueOf(100)).longValue()) // amount in cents
                    .setCurrency("usd")
                    .putMetadata("bookingReference", bookingReference)
                    .build();
            PaymentIntent intent = PaymentIntent.create(params);
            return intent.getClientSecret();

        } catch (Exception e) {
            throw new RuntimeException("Error while creating payment intent");
        }
    }

    public void updatePaymentBooking(PaymentRequest paymentRequest) {

        log.info("Inside updatePaymentBooking method");
        String bookingReference = paymentRequest.getBookingReference();

        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(()-> new NotFoundException("Booking Reference Not Found"));

        Payments payment = new Payments();
        payment.setPaymentGateway(PaymentGateway.STRIPE);
        payment.setAmount(paymentRequest.getAmount());
        payment.setTransactionId(paymentRequest.getTransactionId());
        payment.setPaymentStatus(paymentRequest.isSuccess()? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setBookingReference(bookingReference);
        payment.setUser(booking.getUser());

        if (paymentRequest.isSuccess()) {
            payment.setFailureReason(paymentRequest.getFailureReason());
        }

        paymentRepository.save(payment); //save payment to databases

        //create and send notification
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(booking.getUser().getEmail())
                .type(NotificationType.EMAIL)
                .bookingReference(bookingReference)
                .build();
        log.info("About to send notification inside updatePaymentBooking by sms");

        if(paymentRequest.isSuccess()){
            booking.setPaymentStatus(PaymentStatus.COMPLETED);
            bookingRepository.save(booking); // Update the booking
            notificationDTO.setSubject("Booking Payment Successful");
            notificationDTO.setBody("Congratulation! Your Payment for booking with reference " + bookingReference + " has been successful");
            notificationService.sendEmail(notificationDTO); // send email

        }else {
            booking.setPaymentStatus(PaymentStatus.FAILED);
            bookingRepository.save(booking); // Update the booking

            notificationDTO.setSubject("Booking Payment Failed");
            notificationDTO.setBody("Your Payment for booking with reference " + bookingReference + " failed with reason: " + paymentRequest.getFailureReason() );
            notificationService.sendEmail(notificationDTO); // send email
        }



    }
}
