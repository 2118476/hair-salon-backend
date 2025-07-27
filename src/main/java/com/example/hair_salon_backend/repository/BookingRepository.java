// src/main/java/com/example/hair_salon_backend/repository/BookingRepository.java
package com.example.hair_salon_backend.repository;

import com.example.hair_salon_backend.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStylistIdAndBookingTimeBetween(Long stylistId, LocalDateTime startTime, LocalDateTime endTime);
    List<Booking> findByUserId(Long userId);
   // List<Booking> findAllWithUserDetails();
}
