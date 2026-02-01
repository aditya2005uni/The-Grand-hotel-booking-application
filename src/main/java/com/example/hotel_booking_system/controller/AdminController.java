package com.example.hotel_booking_system.controller;

import com.example.hotel_booking_system.entity.Booking;
import com.example.hotel_booking_system.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/bookings")
    public List<Booking> allBookings() {
        return bookingService.getAllBookings();
    }
}