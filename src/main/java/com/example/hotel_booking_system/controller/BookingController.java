package com.example.hotel_booking_system.controller;

import com.example.hotel_booking_system.dto.BookingRequest;
import com.example.hotel_booking_system.entity.Booking;
import com.example.hotel_booking_system.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<String> bookRoom(@RequestBody BookingRequest req, Authentication authentication) {
        try {
               
            String userEmail = authentication.getName();

            Booking booking = new Booking(null, userEmail, req.getCheckInDate(),
                    req.getCheckOutDate(), req.getRoomType());
            bookingService.createBooking(booking);
            return ResponseEntity.ok("Booking confirmed!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/my-bookings")
    public List<Booking> getMyBookings(Authentication authentication) {
        String userEmail = authentication.getName();
        return bookingService.getBookingsByEmail(userEmail);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id, Authentication authentication) {
        String userEmail = authentication.getName();
        bookingService.deleteBooking(id, userEmail);
        return ResponseEntity.ok("Booking deleted successfully");
    }
}