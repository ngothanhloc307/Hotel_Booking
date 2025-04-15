package com.example.HotelBooking.repositories;

import com.example.HotelBooking.entities.Payments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payments, Long> {
}
