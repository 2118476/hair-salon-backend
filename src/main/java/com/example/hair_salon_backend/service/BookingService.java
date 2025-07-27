package com.example.hair_salon_backend.service;

import com.example.hair_salon_backend.model.Booking;
import com.example.hair_salon_backend.model.SalonService;
import com.example.hair_salon_backend.model.Stylist;
import com.example.hair_salon_backend.model.User;
import com.example.hair_salon_backend.repository.BookingRepository;
import com.example.hair_salon_backend.repository.ServiceRepository;
import com.example.hair_salon_backend.repository.StylistRepository;
import com.example.hair_salon_backend.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private StylistRepository stylistRepository;

    public Booking createBooking(Long userId, Long serviceId, Long stylistId, LocalDateTime bookingTime, String notes) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with ID " + userId));
        SalonService service = serviceRepository.findById(serviceId)
            .orElseThrow(() -> new EntityNotFoundException("Service not found with ID " + serviceId));
        Stylist stylist = stylistRepository.findById(stylistId)
            .orElseThrow(() -> new EntityNotFoundException("Stylist not found with ID " + stylistId));

        // Convert booking time to ZonedDateTime in the system's default time zone
        ZonedDateTime zonedBookingTime = bookingTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault());

        // Ensure the booking time is within working hours (e.g., 9 AM to 6 PM)
        if (zonedBookingTime.getHour() < 9 || zonedBookingTime.getHour() >= 18) {
            throw new IllegalStateException("Selected time slot is outside working hours");
        }

        List<Booking> existingBookings = bookingRepository.findByStylistIdAndBookingTimeBetween(stylistId, zonedBookingTime.withHour(0).withMinute(0).withSecond(0).toLocalDateTime(), zonedBookingTime.withHour(23).withMinute(59).withSecond(59).toLocalDateTime());
        boolean slotTaken = existingBookings.stream().anyMatch(booking -> booking.getBookingTime().isEqual(zonedBookingTime.toLocalDateTime()));

        if (slotTaken) {
            throw new IllegalStateException("Selected time slot is not available");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setService(service);
        booking.setStylist(stylist);
        booking.setBookingTime(zonedBookingTime.toLocalDateTime());
        booking.setNotes(notes);

        return bookingRepository.save(booking);
    }

    public Booking updateBooking(Long bookingId, Long serviceId, Long stylistId, LocalDateTime bookingTime, String notes) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new EntityNotFoundException("Booking not found with ID " + bookingId));

        if (serviceId != null) {
            SalonService service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new EntityNotFoundException("Service not found with ID " + serviceId));
            booking.setService(service);
        }

        if (stylistId != null) {
            Stylist stylist = stylistRepository.findById(stylistId)
                .orElseThrow(() -> new EntityNotFoundException("Stylist not found with ID " + stylistId));
            booking.setStylist(stylist);
        }

        // Convert booking time to ZonedDateTime in the system's default time zone
        ZonedDateTime zonedBookingTime = bookingTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault());

        booking.setBookingTime(zonedBookingTime.toLocalDateTime());
        booking.setNotes(notes);

        return bookingRepository.save(booking);
    }

    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new EntityNotFoundException("Booking not found with ID " + bookingId));
        bookingRepository.delete(booking);
    }

    public List<LocalDateTime> getAvailableSlots(Long stylistId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Booking> bookings = bookingRepository.findByStylistIdAndBookingTimeBetween(stylistId, startTime, endTime);
        List<LocalDateTime> bookedSlots = bookings.stream().map(Booking::getBookingTime).collect(Collectors.toList());

        return Stream.iterate(startTime, date -> date.plusHours(1))
            .limit(startTime.until(endTime, java.time.temporal.ChronoUnit.HOURS))
            .filter(slot -> !bookedSlots.contains(slot))
            .collect(Collectors.toList());
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }
    public List<Booking> getAllBookingsWithUserDetails() {
    List<Booking> bookings = bookingRepository.findAll(); // Assuming this fetches all bookings
    bookings.forEach(booking -> Hibernate.initialize(booking.getUser())); // Ensures user data is fetched if it's lazily loaded
    return bookings;
}

}
