package com.example.hair_salon_backend.controller;

import com.example.hair_salon_backend.model.Booking;
import com.example.hair_salon_backend.service.BookingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestParam Long userId, @RequestParam Long serviceId,
            @RequestParam Long stylistId, @RequestParam String bookingTime, @RequestParam String notes) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime time = LocalDateTime.parse(bookingTime, formatter);
        try {
            Booking booking = bookingService.createBooking(userId, serviceId, stylistId, time, notes);
            return ResponseEntity.ok(booking);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // 409 Conflict for slot taken
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestParam Long serviceId,
            @RequestParam Long stylistId, @RequestParam String bookingTime, @RequestParam String notes) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime time = LocalDateTime.parse(bookingTime, formatter);
        Booking booking = bookingService.updateBooking(id, serviceId, stylistId, time, notes);
        return ResponseEntity.ok(booking);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available-slots")
    public ResponseEntity<List<LocalDateTime>> getAvailableSlots(@RequestParam Long stylistId,
            @RequestParam String startTime, @RequestParam String endTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        LocalDateTime end = LocalDateTime.parse(endTime, formatter);
        List<LocalDateTime> availableSlots = bookingService.getAvailableSlots(stylistId, start, end);
        return ResponseEntity.ok(availableSlots);
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }
    @GetMapping("/all-with-user")
public ResponseEntity<List<Booking>> getAllBookingsWithUserDetails() {
    List<Booking> bookings = bookingService.getAllBookingsWithUserDetails();
    return ResponseEntity.ok(bookings);
}


}
